package com.muke.beans;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mail {

    private String subject;

    private String message;

    private Set<String> receivers;
}
