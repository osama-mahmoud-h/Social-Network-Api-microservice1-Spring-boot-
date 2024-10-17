package com.example.server.dto.request.post;

import com.example.server.dto.request.PaginationRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetRecentPostsRequestDto extends PaginationRequestDto {
}
