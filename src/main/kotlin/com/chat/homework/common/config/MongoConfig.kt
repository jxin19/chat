package com.chat.homework.common.config

import com.chat.homework.message.domain.property.MessageContent
import org.bson.Document
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.mongodb.core.convert.MongoCustomConversions

@Configuration
class MongoConfig {
    private class MessageContentWriteConverter : Converter<MessageContent, Document> {
        override fun convert(source: MessageContent): Document {
            return Document().append("_value", source.value)
        }
    }

    private class MessageContentReadConverter : Converter<Document, MessageContent> {
        override fun convert(source: Document): MessageContent {
            return MessageContent(source.getString("_value"))
        }
    }

    @Bean
    fun customConversions(): MongoCustomConversions {
        return MongoCustomConversions(
            listOf(
                MessageContentReadConverter(),
                MessageContentWriteConverter()
            )
        )
    }
}
