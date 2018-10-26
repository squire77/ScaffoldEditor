package com.pd.modelcg.codegen.model.uml.datatypes;

import com.pd.modelcg.codegen.model.uml.basic.UmlDataType;
import com.pd.modelcg.codegen.model.uml.basic.UmlPackage;
import com.pd.modelcg.codegen.model.uml.basic.UmlType;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "test.modeling.uml.datatype")
public class AslArray extends UmlDataType {
    public static AslArray create(UmlType baseType) {
        AslArray arr = (AslArray) UmlType.getTypeByName("Array[" + baseType.getName() + "]");
        
        if (arr == null) {
            arr = new AslArray(baseType);
            UmlType.registerType(arr);
            UmlPackage.getDefaultPackage().addOwnedType(arr.getID());
        }
        
        return arr;
    }
    
    private AslArray() {        
    }
    
    private AslArray(UmlType baseType) {
        super("Array[" + baseType.getName() + "]");
        this.baseTypeID = baseType.getID();
    }  

    @Override
    public boolean isArray() { return true; }        
    
    public UmlType getBaseType() {
        return UmlType.getType(this.baseTypeID);
    }
    
    public String baseTypeID;
}
