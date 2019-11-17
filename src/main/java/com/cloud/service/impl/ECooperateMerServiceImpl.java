package com.cloud.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.cloud.commons.dto.ECooperateMer;
import com.cloud.commons.response.QueryECooperateMerResponse;
import com.cloud.commons.service.ECooperateMerService;
import com.cloud.service.mapper.ECooperateMerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author: LiuHeYong
 * @create: 2019-05-27
 * @description: ECooperateMerServiceImpl
 **/
@Component
@Service(interfaceClass = ECooperateMerService.class, version = "${dubbo.service.version}", timeout = 60000)
public class ECooperateMerServiceImpl implements ECooperateMerService {

    private static final Logger logger = LoggerFactory.getLogger(ECooperateMerServiceImpl.class);

    @Autowired
    private ECooperateMerMapper eCooperateMerMapper;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public ECooperateMer queryECooperateMerInfo(ECooperateMer eCooperateMer) {
        Optional<ECooperateMer> optDto =
                Optional.ofNullable(Optional.ofNullable(eCooperateMerMapper.selectECooperateMerInfo(eCooperateMer)).orElseGet(ECooperateMer::new));
        return optDto.get();
    }

    @Override
    public QueryECooperateMerResponse queryECooperateMerListPage(ECooperateMer eCooperateMer) throws Exception {
        QueryECooperateMerResponse response = new QueryECooperateMerResponse();
        try {
            List<ECooperateMer> eList = (List<ECooperateMer>) redisTemplate.opsForValue().get("eList");
            if (eList == null) {
                synchronized (this) {
                    if (eList == null) {
                        eList = eCooperateMerMapper.queryECooperateMerListPage();
                        redisTemplate.opsForValue().set("eList",eList,60, TimeUnit.SECONDS);
                        logger.info("从数据库中获取的数据");
                    }
                }
            } else {
                logger.info("从缓存中获取的数据");
            }
            response.seteCooperateMerList(eList);
            return response;
        } catch (Exception e) {
            logger.error("系统异常", e);
            throw new Exception("系统异常");
        }
    }
}
