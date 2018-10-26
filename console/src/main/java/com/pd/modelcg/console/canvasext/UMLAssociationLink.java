package com.pd.modelcg.console.canvasext;

import com.pd.modelcg.codegen.graphics.graphcanvas.Graph;
import com.pd.modelcg.codegen.model.uml.basic.cm.CmAssociation;
import com.pd.modelcg.console.canvasext.form.UMLAssociationForm;

import java.awt.event.MouseEvent;

public class UMLAssociationLink extends UMLLink {         
    // use the UMLLink.create() to ensure initialize() is called   
    UMLAssociationLink(LinkType ltype, Graph graph, UMLEndPoint ep1, UMLEndPoint ep2, CmAssociation assoc) {
        super(ltype, graph, ep1, ep2);
        
        this.setModelElement(assoc);      
    }                              

    @Override
    public void mouseClicked(MouseEvent event) {      
        if (event.getClickCount() == 2) {
            //lazy create the form to save on load time
            if (associationForm == null) {
                this.associationForm = new UMLAssociationForm(assoc);
            }
            
            associationForm.loadValues();
            associationForm.setLocation(event.getXOnScreen(), event.getYOnScreen());
            associationForm.setLocationRelativeTo(null); //center dialog on screen
            associationForm.setVisible(true);
        }
    }            
       
    private UMLAssociationForm      associationForm;         
}
