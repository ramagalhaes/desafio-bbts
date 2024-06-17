package br.com.bbtecno.desafio.core.files;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3FileStorage implements FileStorage {

    private final AmazonS3 s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Override
    public Mono<String> save(Mono<FilePart> file, String fileName) {
        return file.doOnNext(f -> log.info("S3FileStorage::save() - Chegou aqui"))
                .flatMap(filePart -> {
                    Path tempFilePath = Paths.get(System.getProperty("java.io.tmpdir"), filePart.filename());
                    File tempFile = tempFilePath.toFile();
                    return filePart.transferTo(tempFile).then(Mono.just(tempFile));
                })
                .flatMap(f -> Mono.just(s3Client.putObject(bucketName, fileName, f)))
                .doOnSuccess(result -> log.info("S3FileStorage::save() - file saved to S3: [{}]", fileName))
                .flatMap(result -> Mono.just(result.getETag()));
    }

    @Override
    public Mono<String> delete(String fileName) {
        DeleteObjectsRequest request = new DeleteObjectsRequest(bucketName)
                .withKeys(fileName);
        return Mono.just(s3Client.deleteObjects(request))
                .doOnSuccess(result -> log.info("S3FileStorage::delete() - Deleted file with name: {}", result.getDeletedObjects().getFirst()))
                .flatMap(result -> Mono.just(fileName));
    }
}
