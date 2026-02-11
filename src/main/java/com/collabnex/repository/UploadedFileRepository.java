
package com.collabnex.repository;

import com.collabnex.entity.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadedFileRepository
        extends JpaRepository<UploadedFile, Long> {
}
