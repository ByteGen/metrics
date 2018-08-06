package com.bytegen.common.metrics.model;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: xiang
 * Date: 2018/6/29
 * Desc:
 */
public class Tag {
    private Map<String, String> tags;

    private static String TAG_SEPARATOR = ",";
    private static String KV_SEPARATOR = "=";

    private Tag(TagBuilder tagBuilder) {
        tags = new ConcurrentHashMap<>(tagBuilder.tags);
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public String toTagString() {
        if (null == tags || tags.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = tags.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (null != entry.getKey()) {
                builder.append(entry.getKey());
                builder.append(KV_SEPARATOR);
                builder.append(entry.getValue());
            }
            if (!iterator.hasNext()) {
                break;
            }
            builder.append(TAG_SEPARATOR);
        }
        return builder.toString();
    }

    public static class TagBuilder {

        private Map<String, String> tags = new ConcurrentHashMap<String, String>();

        public TagBuilder() {
            //omit
        }

        public TagBuilder put(String tagString) {
            String[] tagList = tagString.trim().split(TAG_SEPARATOR);
            for (String tag : tagList) {
                String[] kv = tag.trim().split(KV_SEPARATOR);
                if (kv.length != 2) continue;
                tags.put(kv[0], kv[1]);
            }
            return this;
        }

        public TagBuilder put(String key, String value) {
            tags.put(key, value);
            return this;
        }

        public TagBuilder put(Tag tag) {
            if (null != tag) {
                tags.putAll(tag.getTags());
            }
            return this;
        }

        public Tag build() {
            return new Tag(this);
        }

    }
}
