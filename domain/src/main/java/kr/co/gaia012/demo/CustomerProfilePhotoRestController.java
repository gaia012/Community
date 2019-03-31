package kr.co.gaia012.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.Callable;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

// 바이너리 데이터 읽고 쓰기.
@Slf4j
@RestController
@RequestMapping(value = "/customers/{id}/photo")
public class CustomerProfilePhotoRestController {
    private File root;

    @Autowired
    CustomerProfilePhotoRestController(@Value("${upload.dir:${user.home}/images") String uploadDir) {
        this.root = new File(uploadDir);
        Assert.isTrue(this.root.exists() || this.root.mkdirs()
                , String.format("The path '%s' must exist.", this.root.getAbsolutePath()));
    }

    @GetMapping
    ResponseEntity<Resource> read(@PathVariable Long id) {
        return Optional.of(new Customer())
                .map(customer -> {
                    File file = fileFor(customer);
                    Assert.isTrue(file.exists(), String.format("file-not-found %s", file.getAbsolutePath()));

                    Resource fileSystemResource = new FileSystemResource(file);
                    return ResponseEntity.ok()
                            .contentType(MediaType.IMAGE_JPEG)
                            .body(fileSystemResource);
                })
                .orElseThrow(() -> new RuntimeException("file not found"));
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    Callable<ResponseEntity<?>> write(@PathVariable Long id, @RequestParam MultipartFile file) throws Exception {
        log.info(String.format("upload-start /customers/%s/photo (%s bytes)", id, file.getSize()));

        return () -> Optional.of(new Customer())
                .map(customer -> {
                    File fileForCustomer = fileFor(customer);

                    try (InputStream in = file.getInputStream();
                         OutputStream out = new FileOutputStream(fileForCustomer)) {
                        FileCopyUtils.copy(in, out);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    URI location = fromCurrentRequest().buildAndExpand(id).toUri();
                    log.info(String.format("upload-finish /customers/%s/photo (%s)", id, location));
                    return ResponseEntity.created(location).build();
                })
                .orElseThrow(() -> new RuntimeException("file not found"));
    }

    private File fileFor(Customer person) {
        return new File(this.root, Long.toString(person.getId()));
    }
}
