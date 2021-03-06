package com.alibaba.datax.transformer;

import com.alibaba.datax.common.element.*;
import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.transformer.support.Hiding;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Created by Liu Kun on 2019/3/14.
 */
public class HidingTransformer extends ComplexTransformer{
    private Object masker;
    String maskMethodId = "";
    String key;
    int columnIndex;

    public HidingTransformer(){
        super.setTransformerName("hiding_transformer");
    }


    public Record evaluate(Record record, Map<String, Object> tContext, Object... paras){
        try {
            if (paras.length < 1) {
                throw new RuntimeException("Hiding transformer 缺少参数");
            }
            columnIndex = (Integer) paras[0];
        } catch (Exception e) {
            throw DataXException.asDataXException(TransformerErrorCode.TRANSFORMER_ILLEGAL_PARAMETER, "paras:" + Arrays.asList(paras).toString() + " => " + e.getMessage());
        }
        Column column = record.getColumn(columnIndex);
        try{
            String oriValue = column.asString();
            if(oriValue == null){
                return  record;
            }
            Hiding masker = new Hiding();
            if(column.getType() == Column.Type.STRING){
                String newValue = masker.mask(column.asString());
                record.setColumn(columnIndex, new StringColumn(newValue));
            }
            else if(column.getType() == Column.Type.DATE){
                Date newValue = masker.mask(column.asDate());
                record.setColumn(columnIndex, new DateColumn(newValue));
            }
            else if(column.getType() == Column.Type.LONG || column.getType()==Column.Type.INT){
                long newValue = masker.mask(column.asLong());
                record.setColumn(columnIndex, new LongColumn(newValue));
            }
            else if(column.getType() == Column.Type.BOOL){
                boolean newValue = ((Hiding) masker).mask(column.asBoolean());
                record.setColumn(columnIndex, new BoolColumn(newValue));
            }
            else if(column.getType() == Column.Type.DOUBLE){
                double newValue = masker.mask(column.asDouble());
                record.setColumn(columnIndex, new DoubleColumn(newValue));
            }
        } catch (Exception e) {
            throw DataXException.asDataXException(TransformerErrorCode.TRANSFORMER_RUN_EXCEPTION, e.getMessage(),e);
        }
        return record;
    }

}
