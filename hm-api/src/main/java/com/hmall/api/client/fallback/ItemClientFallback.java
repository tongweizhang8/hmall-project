package com.hmall.api.client.fallback;

import com.hmall.api.client.ItemClient;
import com.hmall.api.dto.ItemDTO;
import com.hmall.api.dto.OrderDetailDTO;
import com.hmall.common.exception.BizIllegalException;
import com.hmall.common.utils.CollUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

//用来实现限制访问并发量后降级访问的返回逻辑，减低异常率，说白就是给拒绝访问的线程返回信息
@Slf4j
public class ItemClientFallback implements FallbackFactory<ItemClient> {

    @Override
    public ItemClient create(Throwable cause) {
        log.error("ItemClientFallback", cause);
        return new ItemClient() {
            @Override
            public List<ItemDTO> queryItemByIds(Collection<Long> ids) {
                //查询出现异常，返回空集合
                log.error("queryItemByIds fallback", cause);
                return Collections.emptyList();
            }

            @Override
            public void deductStock(List<OrderDetailDTO> items) {
                throw new RuntimeException(cause);
            }
        };
    }
}
