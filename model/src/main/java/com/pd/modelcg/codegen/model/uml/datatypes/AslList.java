package com.pd.modelcg.codegen.model.uml.datatypes;

import com.pd.modelcg.codegen.model.uml.basic.UmlDataType;
import com.pd.modelcg.codegen.model.uml.basic.UmlPackage;
import com.pd.modelcg.codegen.model.uml.basic.UmlType;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "test.modeling.uml.datatype")
public class AslList extends UmlDataType {
    public static AslList create(UmlType baseType) {
        //check if type already exists
        AslList list = (AslList) UmlType.getTypeByName("List<" + baseType.getName() + ">");
        
        if (list == null) {
            list = new AslList(baseType);
            UmlType.registerType(list);
            UmlPackage.getDefaultPackage().addOwnedType(list.getID());
        }
        
        return list;
    }
    
    private AslList() {        
    }
    
    private AslList(UmlType baseType) {
        super("List<" + baseType.getName() + ">");
        this.baseTypeID = baseType.getID();
    }

    @Override
    public boolean isList() { return true; }    
        
    public UmlType getBaseType() {
        return UmlType.getType(this.baseTypeID);
    }
    
    public String baseTypeID;
}
