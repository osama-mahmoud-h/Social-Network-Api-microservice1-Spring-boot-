package com.app.shared.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * The author of a piece of content, as carried on the wire.
 *
 * <p>Deliberately a flattened snapshot rather than a user reference: consumers must be able to
 * render "Ada Lovelace commented on your post" without calling back into main-service.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorData implements Serializable {
    private Long userId;
    private String firstName;
    private String lastName;
}