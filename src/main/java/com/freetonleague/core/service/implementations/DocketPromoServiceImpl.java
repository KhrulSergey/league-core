package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.filter.DocketPromoCreationFilter;
import com.freetonleague.core.domain.model.DocketPromoEntity;
import com.freetonleague.core.domain.model.DocketPromoUsageEntity;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.mapper.DocketPromoMapper;
import com.freetonleague.core.repository.DocketPromoRepository;
import com.freetonleague.core.service.DocketPromoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocketPromoServiceImpl implements DocketPromoService {

    private final DocketPromoRepository docketPromoRepository;
    private final DocketPromoMapper docketPromoMapper;

    @Override
    public List<DocketPromoEntity> getAll() {
        return docketPromoRepository.findAll();
    }

    @Override
    public DocketPromoEntity getById(Long id) {
        return docketPromoRepository.findById(id)
                .orElseThrow(() ->
                        new HttpClientErrorException(HttpStatus.NOT_FOUND,
                                "No promo code found for the specified ID")
                );
    }

    @Override
    @Transactional
    public DocketPromoEntity createByFilter(DocketPromoCreationFilter filter) {
        String generatedCode = generatePromoCode();

        return docketPromoRepository.save(
                docketPromoMapper.fromFilter(filter, generatedCode)
        );
    }

    @Override
    @Transactional
    public void usePromo(String promoCode, User user) {
        DocketPromoEntity docketPromoEntity = docketPromoRepository.findByPromoCode(promoCode)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));


        List<DocketPromoUsageEntity> usages = docketPromoEntity.getUsages();

        if (usages.size() >= docketPromoEntity.getMaxUsages()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "no more usages");
        }

        DocketPromoUsageEntity usageEntity = DocketPromoUsageEntity.builder()
                .promo(docketPromoEntity)
                .user(user)
                .build();

        usages.add(usageEntity);

    }

    private String generatePromoCode() {
        String generatedCode;

        do {
            generatedCode = RandomStringUtils.random(5, true, true);
        } while (docketPromoRepository.existsByPromoCode(generatedCode));

        return generatedCode;
    }

}
