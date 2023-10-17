package com.example.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class WebhookNotificationDTO
{
    private String action;
    private String api_version;
    private String application_id;
    private String date_created;
    private String id;
    private String live_mode;
    private String type;
    private String user_id;
    private Data data;

    @NoArgsConstructor @AllArgsConstructor
    @Getter @Setter
    public static class Data
    {
        private String id;
    }
}
