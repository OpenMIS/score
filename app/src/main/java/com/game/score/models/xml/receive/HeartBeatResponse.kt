package com.game.score.models.xml.receive

import com.game.score.core.IGameMessageModel
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "Body")
@XmlAccessorType(XmlAccessType.FIELD)
data class HeartBeatResponse(
    /**
     * 消息类型
     */
    @XmlAttribute
    override val MessageType: String,


    val HeartBeatResponse: HeartBeatResponseClass
) : IGameMessageModel {
    data class HeartBeatResponseClass(

        @XmlAttribute
        val RcvPort: Int,

        //region 资料
        /*
Document\经验分享\技术\编程语言\Kotlin\Kotlin 序列化-经验.txt
	6）在做xml反序列化成Java/Kotlin对象时，Java/Kotlin对象的属性只有一个时，会报类似如下错误：
jackson-dataformat-xml:2.9.4 - 2.11.1

com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot construct instance of `com.game.score.models.xml.receive.HeartBeatResponse$HeartBeatResponseClass` (although at least one Creator exists): cannot deserialize from Object value (no delegate- or property-based Creator)
 at [Source: (StringReader); line: 3, column: 5] (through reference chain: com.game.score.models.xml.receive.HeartBeatResponse["HeartBeatResponse"])
解决方法：在Java/Kotlin对象里加一个带默认指的属性
         */
        //endregion
        @XmlAttribute
        val Version: String = "1.0"
    )
}