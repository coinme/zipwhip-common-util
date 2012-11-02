package com.zipwhip.format;

import com.zipwhip.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 10/25/12
 * Time: 5:17 PM
 */
public class MultipartFormatterAdapter extends FormatterAdapterBase<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultipartFormatterAdapter.class);

    public MultipartFormatterAdapter(Formatter<String> formatter) {
        super(formatter);
    }

    @Override
    public String format(String input) {
        if (StringUtil.isNullOrEmpty(input)) {
            try {
                return super.format(input);
            } catch (Exception e) {
                LOGGER.error("Weird exception! Ignoring.", e);
            }

            return input;
        }

        StringBuilder sb = new StringBuilder();
        if (input.startsWith(",")) {
            // it's multipart!
            String[] parts = input.split(",");
            for (String part : parts) {
                if (StringUtil.isNullOrEmpty(part)) {
                    continue;
                }

                try {
                    part = super.format(part);
                } catch (Exception e) {
                    LOGGER.debug("Error formatting - short code? " + part);
                }

                sb.append(",").append(part);
            }
        } else {
            try {
                sb.append(super.format(input));
            } catch (Exception e) {
                LOGGER.debug("Error formatting - short code? " + input);
                sb.append(input);
            }
        }

        return sb.toString();
    }
}
