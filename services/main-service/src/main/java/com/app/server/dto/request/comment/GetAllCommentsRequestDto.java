package com.app.server.dto.request.comment;

import com.app.server.dto.request.PaginationRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetAllCommentsRequestDto extends PaginationRequestDto {
    private Long postId;
}
