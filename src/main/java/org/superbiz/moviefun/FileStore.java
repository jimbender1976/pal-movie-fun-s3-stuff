package org.superbiz.moviefun;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;


public class FileStore implements BlobStore {

    @Override
    public void put(Blob blob) throws IOException {
        // ...
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        // ...

        return null;
    }

    @Override
    public void deleteAll() {
        // ...
    }
}
