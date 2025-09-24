package com.msi.springsecExample.Repository;

import com.msi.springsecExample.entity.FileText;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileTextRepository extends JpaRepository<FileText, Long> {}
