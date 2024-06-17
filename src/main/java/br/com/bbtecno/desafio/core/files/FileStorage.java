package br.com.bbtecno.desafio.core.files;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface FileStorage {

    Mono<String> save(Mono<FilePart>file, String fileName);
    Mono<String> delete(String fileName);
}
