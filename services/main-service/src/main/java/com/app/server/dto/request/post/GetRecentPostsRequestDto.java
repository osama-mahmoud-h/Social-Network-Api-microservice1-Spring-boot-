package com.app.server.dto.request.post;

import com.app.server.dto.request.PaginationRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetRecentPostsRequestDto extends PaginationRequestDto {
}
