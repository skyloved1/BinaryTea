package sky.learnspringbinarytea.custom

import org.springframework.dao.DataAccessException
import org.springframework.jdbc.support.SQLExceptionTranslator
import org.springframework.stereotype.Component
import java.sql.SQLException

@Component
class CustomSQLErrorTranslator : SQLExceptionTranslator {
    override fun translate(task: String, sql: String?, ex: SQLException): DataAccessException? {
        return if (ex.errorCode == 123456) {
            CustomSQLException("Translated custom SQL error (code ${ex.errorCode}),msg ${ex.message} ", ex)
        } else {
            null // 交给默认翻译器处理
        }
    }
}


class CustomSQLException : DataAccessException {
    var msg: String? = null
    override var cause: Throwable? = null

    constructor(msg: String?) : super(msg) {
        this.msg = msg
    }

    constructor(msg: String?, cause: Throwable?) : super(msg, cause) {
        this.msg = msg
        this.cause = cause
    }
}

