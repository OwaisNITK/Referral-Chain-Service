package com.onmobile.vol.referralchain.app.rest.service;

import com.onmobile.vol.referralchain.app.dto.PrismCallBackRequestDto;
import com.onmobile.vol.referralchain.app.dto.StatusResponseDto.ResponseStatus;

public interface PrismCallbackService {

	public ResponseStatus prismCallBack(PrismCallBackRequestDto prismCallBackRequestDto);
}
