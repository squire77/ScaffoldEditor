package com.pd.modelcg.console.canvasext;

import com.pd.modelcg.codegen.graphics.graphcanvas.Graph;
import com.pd.modelcg.codegen.graphics.graphcanvas.Link;

public class UMLInheritanceLink extends UMLLink {
    // use the UMLLink.create() to ensure initialize() is called    
    UMLInheritanceLink(LinkType ltype, Graph graph, UMLEndPoint ep1, UMLEndPoint ep2) {
        super(ltype, graph, ep1, ep2);
                
        UMLClassNode clazz1 = (UMLClassNode) ep1.getNode();
        UMLClassNode clazz2 = (UMLClassNode) ep2.getNode();
        
        if ((ep1.getEPType() == UMLEndPoint.EPType.INHERITEE && clazz1.getModelElement().isInterface()) ||
            (ep2.getEPType() == UMLEndPoint.EPType.INHERITEE && clazz2.getModelElement().isInterface())) {
            super.setLineStyle(Link.LineStyle.DASHED);
        } else {
            super.setLineStyle(Link.LineStyle.SOLID);                   
        }      
    } 
}
