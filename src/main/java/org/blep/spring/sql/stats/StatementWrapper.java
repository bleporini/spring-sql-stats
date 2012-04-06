package org.blep.spring.sql.stats;

import lombok.Delegate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Statement;

/**
 * @author blep
 *         Date: 18/03/12
 *         Time: 10:26
 */
@Component
@Scope("prototype")
class StatementWrapper implements Statement {


    StatementWrapper(Statement statement) {
        delegate = statement;
    }

    @Delegate
    private Statement delegate;

}
