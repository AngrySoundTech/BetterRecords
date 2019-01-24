package tech.feldman.betterrecords.api.wire

import tech.feldman.betterrecords.api.connection.RecordConnection

interface IRecordWire {

    val connections: MutableList<RecordConnection>

    fun getName(): String

    val songRadiusIncrease: Float
}
