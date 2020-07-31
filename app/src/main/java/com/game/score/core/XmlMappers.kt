package com.game.score.core

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

class XmlMappers {
    companion object {

        /**
         * 用于接收消息处理的XmlMapper
         */
        val receive = XmlMapper(JacksonXmlModule().apply {
            setDefaultUseWrapper(false)
        }).registerKotlinModule()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true) //映射时不区分大小写
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) //忽略未知的XML元素或属性
            .configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true)!! //忽略根

        /**
         * 用于发送消息处理的XmlMapper
         */
        val send =
            XmlMapper().setAnnotationIntrospector(JaxbAnnotationIntrospector(TypeFactory.defaultInstance())) //希望的结果
    }
}