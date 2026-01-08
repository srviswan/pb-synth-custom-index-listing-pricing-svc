package com.pb.synth.cib.infra.proto;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProtoUtils {

    public static String toJson(Message message) {
        try {
            return JsonFormat.printer().print(message);
        } catch (Exception e) {
            log.error("Error converting Proto to JSON", e);
            throw new RuntimeException(e);
        }
    }

    public static <T extends Message.Builder> void mergeFromJson(String json, T builder) {
        try {
            JsonFormat.parser().ignoringUnknownFields().merge(json, builder);
        } catch (Exception e) {
            log.error("Error merging JSON to Proto", e);
            throw new RuntimeException(e);
        }
    }
}
