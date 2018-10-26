package com.pd.modelcg.console.canvasext;

import com.pd.modelcg.codegen.graphics.graphcanvas.Graph;
import com.pd.modelcg.codegen.model.uml.basic.cm.CmAssociation;
import com.pd.modelcg.console.canvasext.form.UMLAggregationForm;

import java.awt.event.MouseEvent;

public class UMLAggregationLink extends UMLLink {         
    // use the UMLLink.create() to ensure initialize() is called   
    UMLAggregationLink(LinkType ltype, Graph graph, UMLEndPoint ep1, UMLEndPoint ep2, CmAssociation assoc) {
        super(ltype, graph, ep1, ep2);
        
        this.setModelElement(assoc);         
    }                              

    @Override
    public void mouseClicked(MouseEvent event) {      
        if (event.getClickCount() == 2) {
            //lazy create the form to save on load time
            if (aggregationForm == null) {
                this.aggregationForm = new UMLAggregationForm(this, assoc);
            }
            
            aggregationForm.loadValues();
            aggregationForm.setLocation(event.getXOnScreen(), event.getYOnScreen());
            aggregationForm.setLocationRelativeTo(null); //center dialog on screen
            aggregationForm.setVisible(true);
        }
    }            
    
    private UMLAggregationForm      aggregationForm;         
}

