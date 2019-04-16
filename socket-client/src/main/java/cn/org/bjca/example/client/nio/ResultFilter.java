package cn.org.bjca.example.client.nio;

import cn.org.bjca.example.socket.ExampleMessage;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

import java.io.IOException;

/**
 * @author lizhong
 * @create：2019-04-11 上午 09:41
 */
@Slf4j
public class ResultFilter extends BaseFilter {
    /**
     * {@inheritDoc}
     *
     * @param ctx
     */
    @Override
    public NextAction handleRead(FilterChainContext ctx) throws IOException {
        ExampleMessage exampleMessage= ctx.getMessage();
        log.debug( "filter"+":"+
                "head:"
                        + exampleMessage.getHeadByUtf8()
                        + " content:"
                        + exampleMessage.getContentByUtf8());
        ctx.setMessage(exampleMessage);
        return ctx.getInvokeAction();
    }
}
