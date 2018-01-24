package org.superbiz.moviefun.albums;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.Blob;
import org.superbiz.moviefun.BlobStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    private final AlbumsBean albumsBean;

    private BlobStore blobStore;

    public AlbumsController(AlbumsBean albumsBean, BlobStore blobStore) {

        this.albumsBean = albumsBean;
        this.blobStore = blobStore;
    }


    @GetMapping
    public String index(Map<String, Object> model) {
        model.put("albums", albumsBean.getAlbums());
        return "albums";
    }

    @GetMapping("/{albumId}")
    public String details(@PathVariable long albumId, Map<String, Object> model) {
        model.put("album", albumsBean.find(albumId));
        return "albumDetails";
    }

    @PostMapping("/{albumId}/cover")
    public String uploadCover(@PathVariable long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {
        saveUploadToFile(uploadedFile, albumId);

        return format("redirect:/albums/%d", albumId);
    }

    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable long albumId) throws IOException, URISyntaxException {

        Optional<Blob> blob = blobStore.get(String.valueOf(albumId));

        if (blob.isPresent()) {
            InputStream is = blob.get().getInputStream();
            byte[] imageBytes = IOUtils.toByteArray(is);
            HttpHeaders headers = createImageHttpHeaders(is, imageBytes);
            return new HttpEntity<>(imageBytes, headers);
        } else {
            // TODO: Test
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }


    private void saveUploadToFile(MultipartFile uploadedFile, long albumId) throws IOException {

        // What to use as name?
        Blob blob = new Blob(String.valueOf(albumId), uploadedFile.getInputStream(), uploadedFile.getContentType());

        blobStore.put(blob);


        // TODO: How to use file store to delete single
//        targetFile.delete();
//        targetFile.getParentFile().mkdirs();
//        targetFile.createNewFile();
//
//        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
//            outputStream.write(uploadedFile.getBytes());
//        }
    }

    private HttpHeaders createImageHttpHeaders(InputStream is, byte[] imageBytes) throws IOException {
        String contentType = new Tika().detect(is);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(imageBytes.length);
        return headers;
    }


}
