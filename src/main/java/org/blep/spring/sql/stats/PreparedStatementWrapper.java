package org.blep.spring.sql.stats;

import lombok.Delegate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;

/**
 * @author blep
 *         Date: 18/03/12
 *         Time: 11:22
 */
@Component
@Scope("prototype")
class PreparedStatementWrapper extends StatementWrapper implements PreparedStatement{

    @Delegate
    private PreparedStatement delegate;

    PreparedStatementWrapper(PreparedStatement preparedStatement) {
        super(preparedStatement);
        delegate =  preparedStatement;
    }


}
