package com.likelionknu.applyserver.admin.service;

import com.likelionknu.applyserver.admin.data.dto.request.AdminMemoRequestDto;
import com.likelionknu.applyserver.application.data.entity.Application;
import com.likelionknu.applyserver.application.data.exception.ApplicationNotFoundException;
import com.likelionknu.applyserver.application.data.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AdminApplicationService {

    private final ApplicationRepository applicationRepository;

    @Transactional
    public void saveAdminMemo(Long applicationId, AdminMemoRequestDto request) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException());

        application.updateNote(request.getMemo());
    }
}
