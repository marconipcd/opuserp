package com.digital.opuserp.view.util;

import java.lang.reflect.Method;

import com.digital.opuserp.domain.CstCofins;
import com.digital.opuserp.domain.CstIpi;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.util.CstIpiUtil.CstIpiUtilEvent;
import com.digital.opuserp.view.util.CstIpiUtil.CstIpiUtilListerner;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;

public class CstCofinsUtil extends Window {

	JPAContainer<CstCofins> container;
	
	TextField tfBusca;
	Table tb;
	Button btSalvar;
	Button btCancelar;
	
	Integer codEmpresa;
	
	public CstCofinsUtil(boolean modal, boolean center, Integer codEmpresa){
		
		super("Selecione um CST");		
		this.codEmpresa = codEmpresa;
		
		setWidth("830px");
		setHeight("360px");
		setIcon(new ThemeResource("icons/search-32.png"));
		setModal(modal);
		if(center){
			center();
		}
		
		setClosable(false);						
				
		setContent(new VerticalLayout(){
			{
				setSizeFull();
				setMargin(true);
				
				addComponent(buildTextField());
				addComponent(buildTb());
				setExpandRatio(tb, 2);
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSalvar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
			}
		});
	}
	
	public JPAContainer<CstCofins> buildJpaContainer(){
		container = JPAContainerFactory.make(CstCofins.class, ConnUtil.getEntity());
		return container;
	}
	
	public void procurar(String s){
		container.removeAllContainerFilters();
		container.addContainerFilter("descricao", "%"+s+"%", true, false);
		container.applyFilters();
	}
	
	public Table buildTb(){
		tb = new Table(null, buildJpaContainer());
		tb.setWidth("100%");
		tb.setHeight("230px");
		tb.setSelectable(true);
		tb.setColumnHeader("id", "Cod");
		tb.setColumnHeader("descricao", "Descricao");
		tb.setColumnCollapsingAllowed(false);		
		tb.setVisibleColumns(new Object[]{"id","descricao"});
		tb.addListener(new ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()){
					Object ItemId = ((Table)event.getComponent()).getValue();
					Item item = ((Table)event.getComponent()).getItem(ItemId);
					
					EntityItem<CstCofins> entityItem = (EntityItem<CstCofins>)tb.getItem(tb.getValue());
					CstCofins cst_cofins = entityItem.getEntity();
															
					fireEvent(new CstCofinsUtilEvent(getUI(), cst_cofins));
					close();
					
				}
			}
		});
		return tb;
	}
	
	
	public TextField buildTextField() {
		tfBusca = new TextField();
		tfBusca.setWidth("100%");
		tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfBusca.focus();
		tfBusca.addListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				addFilter(event.getText());
			}
		});
		return tfBusca;
	}
	

	public void addFilter(String s) {
		container.removeAllContainerFilters();
		container.addContainerFilter(
				Filters.or(
						new Like("descricao", "%"+s+"%", false)
						));
		container.applyFilters();
	}
	
	
	private Button buildBtSalvar() {
		btSalvar = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
				if(tb.getValue()!= null){
					EntityItem<CstCofins> entityItem = (EntityItem<CstCofins>)tb.getItem(tb.getValue());
					CstCofins cst_cofins = entityItem.getEntity();
						
					fireEvent(new CstCofinsUtilEvent(getUI(), cst_cofins));
					close();
				}	
			}	
		});
		
		ShortcutListener slbtOK = new ShortcutListener("Ok",ShortcutAction.KeyCode.ENTER, null) {

			@Override
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};

		btSalvar.addShortcutListener(slbtOK);
		
		btSalvar.setStyleName("default");
		return btSalvar;
	}

	private Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				close();
			}	
		});
		
			ShortcutListener clTb = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE, null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		btCancelar.addShortcutListener(clTb);
		
		return btCancelar;
	}

	
	
	public void addListerner(CstCofinsUtilListerner target){
		try {
			Method method = CstCofinsUtilListerner.class.getDeclaredMethod("onSelected",CstCofinsUtilEvent.class);
			addListener(CstCofinsUtilEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(CstCofinsUtilListerner target){
		removeListener(CstCofinsUtilEvent.class, target);
	}
	public static class CstCofinsUtilEvent extends Event{

		private CstCofins cst_cofins;
		
		public CstCofinsUtilEvent(Component source, CstCofins cst_cofins) {
			super(source);		
			this.cst_cofins  = cst_cofins;
		}

		public CstCofins getCstIpi() {
			return cst_cofins;
		}		
	}
	public interface CstCofinsUtilListerner{
		public void onSelected(CstCofinsUtilEvent event);
	}
}
