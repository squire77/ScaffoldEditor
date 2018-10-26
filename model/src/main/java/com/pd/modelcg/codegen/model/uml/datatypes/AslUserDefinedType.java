package com.pd.modelcg.codegen.model.uml.datatypes;

import com.pd.modelcg.codegen.model.uml.basic.UmlPackage;
import com.pd.modelcg.codegen.model.uml.basic.UmlPrimitiveType;
import com.pd.modelcg.codegen.model.uml.basic.UmlType;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "test.modeling.uml.datatype")
public class AslUserDefinedType extends UmlPrimitiveType {
    public static AslUserDefinedType create(String name) {
        AslUserDefinedType ut = new AslUserDefinedType(name);
        UmlType.registerType(ut);
        UmlPackage.getDefaultPackage().addOwnedType(ut.getID());
        return ut;
    }
    
    private AslUserDefinedType() {        
    }
    
    private AslUserDefinedType(String name) {
        super(name);
    }    
    
    @Override
    public boolean isUserDefinedType() { return true; }    
}
