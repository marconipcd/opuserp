package com.digital.opuserp.view.util;

import java.awt.Color;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JTextArea;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ServicoDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.Servico;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.Real;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ServicoUtil extends Window {
	
	JPAContainer<Servico> container;
	
	TextField tfBusca;
	Table tb;
	Button btSalvar;
	Button btCancelar;

	Cliente cliente;
	
	public ServicoUtil(boolean modal, boolean center, Cliente c){
		
		
		super("Selecione um Serviço");
		this.cliente = c;
		
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
				hlButtons.addComponent(buildBtSelecionar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
			}
		});
	}
	
	public ServicoUtil(boolean modal, boolean center){
		
		
		super("Selecione um Serviço");
		
		
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
				hlButtons.addComponent(buildBtSelecionar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
			}
		});
	}
		
	public JPAContainer<Servico> buildJpaContainer(){
		container = JPAContainerFactory.make(Servico.class, ConnUtil.getEntity());	
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("status", "ATIVO"));
				
		container.sort(new String[]{"nome"},new boolean[]{true});
		return container;
	}
		
	public Table buildTb(){
		tb = new Table(null, buildJpaContainer()){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				
				if(colId.equals("valor_venda")){
					EntityItem <Servico> item = (EntityItem <Servico>)tb.getItem(rowId);
					Servico servico = item.getEntity();

					if(cliente != null && cliente.getTabela_preco() != null && servico.getOutras_tb_desc() != null && servico.getOutras_tb_desc().equals("SIM")){
						
						double percentual = cliente.getTabela_preco().getDesconto() / 100;
						double valor_final = new Double(tb.getItem(rowId).getItemProperty(colId).getValue().toString())  - (percentual * new Double(tb.getItem(rowId).getItemProperty(colId).getValue().toString()));

						return "R$ "+Real.formatDbToString(String.valueOf(valor_final));
					}else{
						return "R$ "+Real.formatDbToString(tb.getItem(rowId).getItemProperty(colId).getValue().toString());
					}
				}				

				if(colId.equals("id")){
						
						if(tb.getItem(rowId).getItemProperty("id").getValue() != null){						
							return ((Integer)tb.getItem(rowId).getItemProperty("id").getValue()).toString();				
						}					
				}
				return super.formatPropertyValue(rowId, colId, property);
					
			}		
		};
		
		tb.setCellStyleGenerator(new Table.CellStyleGenerator() {
			
			@Override
			public String getStyle(Table source, Object itemId, Object propertyId) {
				
				 if (propertyId == null)
	                    return "row-header-default";               

				 	EntityItem <Servico> item = (EntityItem<Servico>) source.getItem(itemId); 
				 	Servico servico = item.getEntity();
				 	
				 		if(cliente != null && cliente.getTabela_preco() != null && servico.getOutras_tb_desc() != null && servico.getOutras_tb_desc().equals("SIM")){
							return "row-header-crm-em-tratamento";  
						}else{
							return "row-header-default";      															
						}
					}
				});
		
		
		tb.setWidth("100%");
		tb.setHeight("230px");
		tb.setSelectable(true);
		tb.setColumnHeader("id", "Código");
		tb.setColumnHeader("nome", "Nome");
		tb.setColumnHeader("valor_venda", "Valor");
		tb.setColumnAlignment("valor_venda", Align.RIGHT);
		
		//tb.setConverter("id", null);
		
		
		tb.setVisibleColumns(new Object[]{"id","nome","valor_venda"});
		tb.addListener(new ItemClickListener() {
			
			Servico servico;
			
			@Override
			public void itemClick(ItemClickEvent event) {
				if(event.isDoubleClick()){
					Object ItemId = ((Table)event.getComponent()).getValue();
					Item item = ((Table)event.getComponent()).getItem(ItemId);
					
					if(item != null && item.getItemProperty("id") != null && item.getItemProperty("id").getValue()!=null){
							servico = ServicoDAO.find((Integer)item.getItemProperty("id").getValue());																	
					}
					fireEvent(new ServicoEvent(getUI(), servico));
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
				
				try{
					addFilter(Integer.parseInt(event.getText()));
				}catch(Exception e){
					addFilter(event.getText());				
				}
			}
		});
		return tfBusca;
	}
	

	public void addFilter(String s) {
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("status", "ATIVO"));
			
		
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		for(Object c:collums){		 
			
			if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == String.class){					   	
				filtros.add(new Like(c.toString(), "%"+s+"%", false));
			}			
		}
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();		
		container.sort(new String[]{"nome"},new boolean[]{true});
	}
	
	public void addFilter(Integer s) {
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		container.addContainerFilter(Filters.eq("status", "ATIVO"));
			
		Object[] collums = tb.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		for(Object c:collums){		 
			
			if(!tb.isColumnCollapsed(c.toString()) && container.getType(c.toString()) == Integer.class){					   	
				filtros.add(new Like(c.toString(), "%"+s+"%", false));
			}			
		}
		
		container.addContainerFilter(Filters.or(filtros));
		container.applyFilters();		
		container.sort(new String[]{"nome"},new boolean[]{true});
	}
	
	
	private Button buildBtSelecionar() {
		btSalvar = new Button("Ok", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					
				if(tb.getValue()!= null){
					Item item = tb.getItem(tb.getValue());			
					Servico material = ServicoDAO.find((Integer)item.getItemProperty("id").getValue());
				
					fireEvent(new ServicoEvent(getUI(), material));
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

	
	
	public void addListerner(ServicoListerner target){
		try {
			Method method = ServicoListerner.class.getDeclaredMethod("onSelected",ServicoEvent.class);
			addListener(ServicoEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("Método não Encontrado!");
		}
	}
	public void removeListerner(ServicoListerner target){
		removeListener(ServicoEvent.class, target);
	}
	public static class ServicoEvent extends Event{

		private Servico servico;
		
		public ServicoEvent(Component source, Servico servico) {
			super(source);		
			this.servico  = servico;
		}

		public Servico getServico() {
			
			return servico;
		}		
	}
	public interface ServicoListerner{
		public void onSelected(ServicoEvent event);
	}
}
