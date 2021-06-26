package com.shan.store.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.util.BinaryUtils;
import com.shan.store.dto.KeyAndValueResDto;
import com.shan.store.dto.UploadPolicyResDto;
import com.shan.store.service.StoreService;
import com.shan.store.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * S3 存储服务
 */
@Service
public class S3StoreServiceImpl implements StoreService {

    /**
     * oss服务地址
     */
    @Value("${s3.endpoint}")
    private String endpoint;

    @Value("${s3.access.key}")
    private String accessKey;

    @Value("${s3.secret.key}")
    private String secretKey;

    @Value("${s3.region}")
    private String region;

    /**
     * 私有存储桶
     */
    @Value("${s3.private.bucket}")
    private String privateBucket;

    /**
     * 公有存储桶
     */
    @Value("${s3.public.bucket}")
    private String publicBucket;

    /**
     * s3 客户端
     */
    private AmazonS3 s3Client;

    /**
     * 简单上传凭证过期时间
     */
    private static final Long SIMPLE_UPLOAD_TOKEN_EXPIRE_SECONDS = 7200L;

    /**
     * 私有下载链接过期时间
     */
    private static final Long DOWNLOAD_EXPIRED_EXPIRE_SECONDS = 3600L;

    @PostConstruct
    private void init() {
        // 初始化s3客户端
        ClientConfiguration clientConfig = new com.amazonaws.ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTP);
        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new
                AwsClientBuilder.EndpointConfiguration(endpoint, region);
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        s3Client = AmazonS3ClientBuilder.standard()
                .withClientConfiguration(clientConfig)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withEndpointConfiguration(endpointConfiguration)
                .build();
    }

    /**
     * 获取存储桶名称
     */
    private String getBucketName(boolean isPrivate) {
        return isPrivate ? privateBucket : publicBucket;
    }

    @Override
    public String getDownloadUrl(String fileKey, boolean isPrivate) {
        String downloadUrl;
        String bucketName = this.getBucketName(isPrivate);
        if (isPrivate) {
            Date expireDate = Date.from(Instant.now().plusSeconds(DOWNLOAD_EXPIRED_EXPIRE_SECONDS));
            downloadUrl = s3Client.generatePresignedUrl(bucketName, fileKey, expireDate).toString();
        } else {
            downloadUrl = endpoint + "/" + fileKey;
        }
        return downloadUrl;
    }

    @Override
    public UploadPolicyResDto getFormUploadPolicy(boolean isPrivate) {
        String bucketName = this.getBucketName(isPrivate);
        String fileKey = UUID.randomUUID().toString();
        UploadPolicyResDto resDto = new UploadPolicyResDto();
        resDto.setUploadUrl(endpoint + "/" + bucketName);
        resDto.setFormFields(generateFormFields(bucketName, fileKey));
        return resDto;
    }

    /**
     * 生成表单上传字段
     */
    private List<KeyAndValueResDto> generateFormFields(String bucketName, String fileKey) {
        Date expireDate = TimeUtils.expireDateTime(SIMPLE_UPLOAD_TOKEN_EXPIRE_SECONDS);
        String expiration = TimeUtils.getISO8601Timestamp(expireDate);
        String date = TimeUtils.getISO8601TimeWithoutSplit(expireDate);
        String day = date.split("T")[0];
        String credential = accessKey + "/" + day + "/" + region + "/s3/aws4_request";
        String policy = this.generatePolicy(bucketName, expiration, date, credential);
        String signature = calculateSignature(policy, day, accessKey, region);
        List<KeyAndValueResDto> formFields = new ArrayList<>();
        formFields.add(new KeyAndValueResDto("key", fileKey));
        formFields.add(new KeyAndValueResDto("policy", policy));
        formFields.add(new KeyAndValueResDto("x-amz-signature", signature));
        formFields.add(new KeyAndValueResDto("x-amz-algorithm", "AWS4-HMAC-SHA256"));
        formFields.add(new KeyAndValueResDto("x-amz-credential", credential));
        formFields.add(new KeyAndValueResDto("x-amz-date", date));
        formFields.add(new KeyAndValueResDto("success_action_status", "200"));
        return formFields;
    }

    /**
     * 生成policy
     */
    private String generatePolicy(String bucketName, String expiration, String date, String credential) {

        JSONObject policy = new JSONObject();
        policy.put("expiration", expiration);

        JSONArray conditions = new JSONArray();

        JSONObject bucketJson = new JSONObject();
        bucketJson.put("bucket", bucketName);
        conditions.add(bucketJson);

        JSONArray keyPrefixArr = new JSONArray();
        keyPrefixArr.add("starts-with");
        keyPrefixArr.add("$key");
        keyPrefixArr.add("");
        conditions.add(keyPrefixArr);

        JSONObject credentialJson = new JSONObject();
        credentialJson.put("x-amz-credential", credential);
        conditions.add(credentialJson);

        JSONObject algorithmJson = new JSONObject();
        algorithmJson.put("x-amz-algorithm", "AWS4-HMAC-SHA256");
        conditions.add(algorithmJson);

        JSONObject dateJson = new JSONObject();
        dateJson.put("x-amz-date", date);
        conditions.add(dateJson);

        JSONArray redirectArr = new JSONArray();
        redirectArr.add("eq");
        redirectArr.add("$success_action_status");
        redirectArr.add("200");
        conditions.add(redirectArr);

        JSONArray mimeTypeJson = new JSONArray();
        mimeTypeJson.add("starts-with");
        mimeTypeJson.add("$content-type");
        mimeTypeJson.add("");
        conditions.add(mimeTypeJson);

        policy.put("conditions", conditions);

        return BinaryUtils.toBase64(policy.toJSONString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 计算签名
     */
    private String calculateSignature(String encodePolicy, String dateStr, String secretKey, String region) {
        try {
            byte[] kSecret = ("AWS4" + secretKey).getBytes(StandardCharsets.UTF_8);
            byte[] kDate = this.hmacSHA256(dateStr, kSecret);
            byte[] kRegion = this.hmacSHA256(region, kDate);
            byte[] kService = this.hmacSHA256("s3", kRegion);
            byte[] keyBytes = this.hmacSHA256("aws4_request", kService);
            return this.byte2hex(this.hmacSHA256(encodePolicy, keyBytes));
        } catch (Exception e) {
            System.out.println("计算签名失败" + e.getMessage());
            return null;
        }
    }

    /**
     * 执行加密
     */
    private byte[] hmacSHA256(String data, byte[] key) throws Exception {
        String algorithm = "HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 将二进制转换为小写十六进制
     */
    private String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                hs.append('0');
            }
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }

}
