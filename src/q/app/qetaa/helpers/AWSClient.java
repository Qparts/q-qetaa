package q.app.qetaa.helpers;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AWSClient {

    private static final String MIME_TYPE = "image/png";



    public static void uploadImage(String imageString, String fileName, String bucket){
        try {
            byte[] bytes = Helper.base64ToByteArray(imageString);
            doUpload(bytes,fileName, bucket);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    //run asynchronously
    public static void uploadImage(InputStream is, String fileName, String bucket){
        try {
            byte[] bytes = Helper.inputStreamToBytesArray(is);
            doUpload(bytes, fileName, bucket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        private static void doUpload(byte[] bytes, String fileName, String bucket){
            CompletableFuture.runAsync(()->{
                try {
                    AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(AppConstants.AWS_REGION).build();
                    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentType(MIME_TYPE);
                    metadata.addUserMetadata("x-amz-meta-title", "someTitle");
                    metadata.setContentLength(bytes.length);
                    PutObjectRequest request = new PutObjectRequest(bucket, fileName, bais , metadata);
                    request.setCannedAcl(CannedAccessControlList.BucketOwnerFullControl);
                    s3.putObject(request);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            });

        }


        public static Map retreiveImage(String fileName, String bucket){
            AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(AppConstants.AWS_REGION).build();
            // Get an object and print its contents.
            S3Object fullObject = s3.getObject(new GetObjectRequest(bucket, fileName));
            Map map = new HashMap();
            map.put("size", fullObject.getObjectMetadata().getContentLength());
            map.put("inputStream", fullObject.getObjectContent());
            return map;

        }

}
