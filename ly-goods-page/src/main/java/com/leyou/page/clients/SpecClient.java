package com.leyou.page.clients;

import com.leyou.item.api.SpecApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface SpecClient  extends SpecApi {
}
