package org.superbiz.moviefun;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.tika.io.IOUtils;

import javax.swing.text.html.Option;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class S3Store implements BlobStore {

    private AmazonS3Client s3Client;

    private String s3BucketName;

    public S3Store(AmazonS3Client s3Client, String s3BucketName) {
        this.s3Client = s3Client;
        this.s3BucketName = s3BucketName;
    }

    @Override
    public void put(Blob blob) throws IOException {

        ObjectMetadata meta = new ObjectMetadata();
        byte[] bytes = IOUtils.toByteArray(blob.getInputStream());
        int contentLength = bytes.length;
        meta.setContentLength(contentLength);
        meta.setContentType(blob.getContentType());

        InputStream is = new ByteArrayInputStream(bytes);
        s3Client.putObject(s3BucketName, blob.getName(), is, meta);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {

        S3Object s3Object = s3Client.getObject(s3BucketName, name);

        if (s3Object == null) {
            return Optional.empty();
        } else {
            Blob result = new Blob(name, s3Object.getObjectContent(), s3Object.getObjectMetadata().getContentType());
            return Optional.of(result);
        }

    }

    @Override
    public void deleteAll() {
        // TODO:

    }
}
