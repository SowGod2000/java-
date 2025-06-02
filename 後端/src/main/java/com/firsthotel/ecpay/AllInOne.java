package com.firsthotel.ecpay;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Set;

import com.firsthotel.ecpay.EcpayFunction;
import com.firsthotel.ecpay.ErrorMessage;
import com.firsthotel.ecpay.EcpayException;

public class AllInOne extends AllInOneBase {

    public AllInOne(String log4jPropertiesPath) {
        super(); // 不載入 log4j
    }

    public String aioCheckOut(AioCheckOutALL obj, InvoiceObj invoice) {
        obj.setPlatformID(PlatformID);
        obj.setMerchantID(MerchantID);
        obj.setInvoiceMark(invoice == null ? "N" : "Y");

        if (ignorePayment.length > 0) {
            String ignoreParam = Arrays.toString(ignorePayment)
                    .replaceAll(", ", "#")
                    .replace("[", "")
                    .replace("]", "");
            obj.setIgnorePayment(ignoreParam);
        }

        StringBuilder out = new StringBuilder();
        Hashtable<String, String> fieldValue = EcpayFunction.objToHashtable(obj);

        if (invoice != null) {
            Hashtable<String, String> invoiceField = EcpayFunction.objToHashtable(invoice);
            fieldValue.putAll(invoiceField);
        }

        String checkMacValue = EcpayFunction.genCheckMacValue(HashKey, HashIV, fieldValue);
        fieldValue.put("CheckMacValue", checkMacValue);

        out.append("<form id=\"allPayAPIForm\" action=\"" + aioCheckOutUrl + "\" method=\"post\">");
        for (String key : fieldValue.keySet()) {
            out.append("<input type=\"hidden\" name=\"" + key + "\" value=\"" + fieldValue.get(key) + "\">");
        }
        out.append("<script>document.getElementById('allPayAPIForm').submit();</script>");
        out.append("</form>");

        return out.toString();
    }
}
