package com.digital.opuserp.view.modulos.relatorio.Cliente;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.CategoriasDAO;
import com.digital.opuserp.dao.ComoNosConheceuDAO;
import com.digital.opuserp.domain.Categorias;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ComoNosConheceu;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.view.modulos.relatorio.SearchParameters;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Compare.Greater;
import com.vaadin.data.util.filter.Compare.GreaterOrEqual;
import com.vaadin.data.util.filter.Compare.Less;
import com.vaadin.data.util.filter.Compare.LessOrEqual;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

public class NovoRelatorioCliente extends Window {

	private String valorFiltro;
	private String valorOperador;
	private VerticalLayout vlFiltros;
	private HorizontalLayout hlFiltro;
	private ComboBox cbFiltro;
	private ComboBox cbOperador;
	private TextField tfValorFiltro;
	private ComboBox cBValorFiltro;
	private PopupDateField dFValorFiltro;
	
	
	private HorizontalLayout hlFiltroRoot;
	private List<SearchParameters> listaParametros = new ArrayList<SearchParameters>();
	private JPAContainer<Cliente> container;
	private ComboBox cbOrdenacao;
	private ComboBox cbTipoTabela;
	private ComboBox cbOrientacao;
	private ComboBox cbResumo;
	
	private Button btAdd;	
	private Button btSalvar;
		
	private VerticalLayout vlRoot;
	
	private String ordenacao;
	private String orientacao;
	private String tipo;
	private String resumo;
	
	
	public NovoRelatorioCliente(String title, boolean modal){
			
		setWidth("890px");
		
		container = buildContainer();
		
		setCaption(title);
		setModal(modal);
		setResizable(false);
		setClosable(false);
		center();
		
		vlRoot = new VerticalLayout();	
		vlRoot.setWidth("100%");
		vlRoot.setMargin(true);
		vlRoot.setStyleName("border-form");
		
		
		setContent(new VerticalLayout(){
			{
				setWidth("100%");
				setMargin(true);
				addComponent(vlRoot);
				
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSalvar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		buildLayout();
	}
	
	
	
	public NovoRelatorioCliente(String title, boolean modal,String tipo,String orientacao,String ordenacao,String resumo, List<SearchParameters> listaParametros){
		
		this.ordenacao = ordenacao;
		this.tipo = tipo;
		this.orientacao = orientacao;
		this.resumo = resumo;
		
		this.listaParametros = listaParametros;
		
		
		
		setWidth("890px");
		container = buildContainer();
		
		setCaption(title);
		setModal(modal);
		setResizable(false);
		setClosable(false);
		center();
		
		vlRoot = new VerticalLayout();	
		vlRoot.setWidth("100%");
		vlRoot.setMargin(true);
		vlRoot.setStyleName("border-form");
		
		
		setContent(new VerticalLayout(){
			{
				setWidth("100%");
				setMargin(true);
				addComponent(vlRoot);
				
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.addComponent(buildBtCancelar());
				hlButtons.addComponent(buildBtSalvar());
				
				addComponent(hlButtons);
				setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
			}
		});
		buildLayout();
	}
	
	
	
	public JPAContainer<Cliente> buildContainer(){
		container = new JPAContainerFactory().makeBatchable(Cliente.class, ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa", OpusERP4UI.getEmpresa().getId()));
		container.addNestedContainerProperty("categoria.nome");
		container.addNestedContainerProperty("como_nos_conheceu.nome");
		container.addNestedContainerProperty("endereco_principal.cep");
		container.addNestedContainerProperty("endereco_principal.endereco");
		container.addNestedContainerProperty("endereco_principal.numero");
		container.addNestedContainerProperty("endereco_principal.cidade");
		container.addNestedContainerProperty("endereco_principal.bairro");
		container.addNestedContainerProperty("endereco_principal.uf");
		container.addNestedContainerProperty("endereco_principal.pais");
		container.addNestedContainerProperty("endereco_principal.complemento");
		container.addNestedContainerProperty("endereco_principal.referencia");
		
		return container;
	}
	
	public void buildLayout(){
					
		vlRoot.addComponent(
			new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
													
				cbTipoTabela = new ComboBox("Tipo");
				cbTipoTabela.setWidth("175px");
				cbTipoTabela.focus();
				cbTipoTabela.setNullSelectionAllowed(false);
				cbTipoTabela.addItem("COLUNA ??NICA");
				cbTipoTabela.addItem("MULTI COLUNA");
				cbTipoTabela.setStyleName("caption-align");
				cbTipoTabela.setRequired(true);
				
				if(tipo != null)
				{
					cbTipoTabela.select(tipo);
				}
			
				addComponent(cbTipoTabela);
			}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					cbOrientacao = new ComboBox("Orienta????o");
					cbOrientacao.setWidth("175px");
					cbOrientacao.setNullSelectionAllowed(false);
					cbOrientacao.addItem("RETRATO");
					cbOrientacao.addItem("PAISAGEM");
					cbOrientacao.setStyleName("caption-align");
					cbOrientacao.setRequired(true);
					
					if(orientacao != null){
						cbOrientacao.select(orientacao);						
					}
				
					addComponent(cbOrientacao);
				}
		});
		
		
		
		vlRoot.addComponent(
			new FormLayout(){					
			{
				setStyleName("form-cutom");
				setMargin(true);
				setSpacing(true);
													
				cbOrdenacao = new ComboBox("Ordena????o");
				cbOrdenacao.setWidth("175px");
				cbOrdenacao.setNullSelectionAllowed(false);
				cbOrdenacao.setRequired(true);
				cbOrdenacao.addItem("C??digo");
				cbOrdenacao.addItem("Nome/Raz??o Social");
				cbOrdenacao.addItem("CPF/CNPJ");
				cbOrdenacao.addItem("Tipo Pessoa");
				cbOrdenacao.addItem("Inscri????o Estadual/RG");
				cbOrdenacao.addItem("Nome Fantasia");
				cbOrdenacao.addItem("Contato");
				cbOrdenacao.addItem("Sexo");
				cbOrdenacao.addItem("Data de Nascimento");
				cbOrdenacao.addItem("Telefone Principal"); // telefone1
				cbOrdenacao.addItem("Telefone Alternativo"); // telefone2
				cbOrdenacao.addItem("Telefone Alternativo 2"); // celular 1
				cbOrdenacao.addItem("Telefone Alternativo 3"); // celular 2
				cbOrdenacao.addItem("Email Principal");
				cbOrdenacao.addItem("Email Alternativo");
				cbOrdenacao.addItem("Como nos Conheceu?");
				cbOrdenacao.addItem("OBS");
				cbOrdenacao.addItem("Data de Cadastro");
				cbOrdenacao.addItem("Data de Altera????o");
				cbOrdenacao.addItem("Tratamento");
				cbOrdenacao.addItem("Nome Fantasia");
				cbOrdenacao.addItem("Contato");
				cbOrdenacao.addItem("Telefone Alternativo 2");
				cbOrdenacao.addItem("Telefone Alternativo 3");
				cbOrdenacao.addItem("Email Principal");
				cbOrdenacao.addItem("Email Alternativo");
				cbOrdenacao.addItem("Categoria");
				cbOrdenacao.addItem("Como Quer Ser Chamado");
				cbOrdenacao.addItem("Status");
				cbOrdenacao.addItem("CEP");
				cbOrdenacao.addItem("Endere??o");
				cbOrdenacao.addItem("N??mero");
				cbOrdenacao.addItem("Bairro");
				cbOrdenacao.addItem("Cidade");
				cbOrdenacao.addItem("Pais");
				cbOrdenacao.addItem("Complemento");
				cbOrdenacao.addItem("Referencia");
				cbOrdenacao.addItem("Black List");
				
				cbOrdenacao.setStyleName("caption-align");
				
				
				if(ordenacao != null){
					cbOrdenacao.select(ordenacao);
				}
			
				addComponent(cbOrdenacao);
			}
		});

		
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
														
					cbResumo = new ComboBox("Resumo por");
					cbResumo.setWidth("175px");
					cbResumo.setNullSelectionAllowed(false);
					cbResumo.setRequired(true);
//					cbResumo.addItem("C??digo");
					cbResumo.addItem("Nome/Raz??o Social");
					cbResumo.addItem("CPF/CNPJ");
					cbResumo.addItem("Tipo Pessoa");
					cbResumo.addItem("Inscri????o Estadual/RG");
					cbResumo.addItem("Nome Fantasia");
					cbResumo.addItem("Contato");
					cbResumo.addItem("Sexo");
					cbResumo.addItem("Data de Nascimento");
					cbResumo.addItem("Telefone Principal"); // telefone1
					cbResumo.addItem("Telefone Alternativo"); // telefone2
					cbResumo.addItem("Telefone Alternativo 2"); // celular 1
					cbResumo.addItem("Telefone Alternativo 3"); // celular 2
					cbResumo.addItem("Email Principal");
					cbResumo.addItem("Email Alternativo");
					cbResumo.addItem("Como nos Conheceu?");
					cbResumo.addItem("OBS");
					cbResumo.addItem("Data de Cadastro");
					cbResumo.addItem("Data de Altera????o");
					cbResumo.addItem("Tratamento");
					cbResumo.addItem("Nome Fantasia");
					cbResumo.addItem("Contato");
					cbResumo.addItem("Telefone Alternativo 2");
					cbResumo.addItem("Telefone Alternativo 3");
					cbResumo.addItem("Email Principal");
					cbResumo.addItem("Email Alternativo");
					cbResumo.addItem("Categoria");
					cbResumo.addItem("Como Quer Ser Chamado");
					cbResumo.addItem("Status");
					cbResumo.addItem("CEP");
					cbResumo.addItem("Endere??o");
					cbResumo.addItem("N??mero");
					cbResumo.addItem("Bairro");
					cbResumo.addItem("Cidade");
					cbResumo.addItem("Pais");
					cbResumo.addItem("Complemento");
					cbResumo.addItem("Referencia");
					cbResumo.addItem("Black List");
					cbResumo.setStyleName("caption-align");
					cbResumo.addStyleName("margin-bottom-20");
					
					if(resumo != null){
						cbResumo.select(resumo);						
					}
				
					addComponent(cbResumo);
				}
		});
					
														
		TabSheet ts_principal = new TabSheet();
	    TabSheet.Tab t;
	    t = ts_principal.addTab(new FormLayout(){
	    		{
		    		setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					valorFiltro = "Nome/Raz??o Social";
					valorOperador = "IGUAL";
									
									
					// Filtros
					vlFiltros = new VerticalLayout();
					vlFiltros.addStyleName("margin-20");
					vlFiltros.setSizeUndefined();

					
					if(listaParametros != null && listaParametros.size() >0){
						
						for (SearchParameters s : listaParametros) {
							
							hlFiltro = new HorizontalLayout();
							hlFiltro.addComponent(buildCbFiltro(s.getCampo()));
							hlFiltro.addComponent(buildCbOperador(s.getOperador()));
							
							
							if(selectHeader(s.getCampo()).equals("Sexo") || selectHeader(s.getCampo()).equals("Tipo Pessoa") || selectHeader(s.getCampo()).equals("Como nos Conheceu?") || selectHeader(s.getCampo()).equals("Categoria") || selectHeader(s.getCampo()).equals("Status")){							
								hlFiltro.addComponent(buildcbValorFiltro(s.getValor(),selectHeader(s.getCampo()),true));
							}else if(selectHeader(s.getCampo()).equals("Data de Cadastro") || selectHeader(s.getCampo()).equals("Data de Altera????o") || selectHeader(s.getCampo()).equals("Data de Nascimento")){
								hlFiltro.addComponent(buildDfValorFiltro(s.getValor(), selectHeader(s.getCampo()),true));
							}else{
								hlFiltro.addComponent(buildTfValorFiltro(s.getValor(),true));
							}
							
							
							hlFiltro.addComponent(buildBtAdd(true, s.getId()));
							
							hlFiltroRoot = new HorizontalLayout();
							hlFiltroRoot.setWidth("100%");
							
							vlFiltros.addComponent(hlFiltro);
						}
							
							hlFiltro = new HorizontalLayout();
							hlFiltro.addComponent(buildCbFiltro(null));
							hlFiltro.addComponent(buildCbOperador(null));
							hlFiltro.addComponent(buildTfValorFiltro(null,false));
							hlFiltro.addComponent(buildBtAdd(false, null));
															
							hlFiltroRoot = new HorizontalLayout();
							hlFiltroRoot.setWidth("100%");
	
							vlFiltros.addComponent(hlFiltro);
						
					}else{
						hlFiltro = new HorizontalLayout();
						hlFiltro.addComponent(buildCbFiltro(null));
						hlFiltro.addComponent(buildCbOperador(null));
						hlFiltro.addComponent(buildTfValorFiltro(null,false));
						hlFiltro.addComponent(buildBtAdd(false, null));
														
						hlFiltroRoot = new HorizontalLayout();
						hlFiltroRoot.setWidth("100%");

						vlFiltros.addComponent(hlFiltro);
					}
					
					
					hlFiltroRoot.addComponent(vlFiltros);
					//hlFiltroRoot.setComponentAlignment(vlFiltros, Alignment.TOP_LEFT);
					
									
					addComponent(hlFiltroRoot);			
				
				}
				
	    	
	    }, "Filtro");
	
		vlRoot.addComponent(ts_principal);
			
		
	}

	private TextField buildTfValorFiltro(String v, final boolean editing) {	
		tfValorFiltro = new TextField();
		tfValorFiltro.setWidth("404px");
		tfValorFiltro.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfValorFiltro.setImmediate(true);
		tfValorFiltro.setTextChangeEventMode(TextChangeEventMode.LAZY);
		tfValorFiltro.addTextChangeListener(new FieldEvents.TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				if(cbFiltro.getValue() != null){
					String coluna = cbFiltro.getValue().toString();
					if(!event.getText().isEmpty() && !editing){
											
						String idButton = String.valueOf(listaParametros.size())+((TextField) event.getComponent()).getValue(); 
						btAdd.setId(idButton);
						btAdd.setEnabled(true);
						
					}else{
						btAdd.setEnabled(false);
					}
				}else{
					btAdd.setEnabled(false);
				}
				
			}
		});
	
		tfValorFiltro.addListener(new FieldEvents.BlurListener() {
			
			@Override
			public void blur(BlurEvent event) {
				
				if(cbFiltro.getValue() != null){
					String coluna = cbFiltro.getValue().toString();
					if(!tfValorFiltro.getValue().isEmpty()){
												
						String idButton = String.valueOf(listaParametros.size())+((TextField) event.getComponent()).getValue(); 
						btAdd.setId(idButton);
						btAdd.setEnabled(true);
						
					}else{
						btAdd.setEnabled(false);
					}
				}else{
					btAdd.setEnabled(false);
				}
			}
		});
		
		
		if(v != null){
			tfValorFiltro.setValue(v);
			tfValorFiltro.setReadOnly(true);
		}
				
		return tfValorFiltro;
	}
	
	private PopupDateField buildDfValorFiltro(String v, String coluna, final boolean editing){
		dFValorFiltro = new PopupDateField();
		dFValorFiltro.setWidth("380px");
		dFValorFiltro.setImmediate(true);
		dFValorFiltro.addStyleName("margin-right-20");
		dFValorFiltro.setLenient(true);
		dFValorFiltro.setTextFieldEnabled(true);

		
		if(!coluna.equals("Data de Nascimento")){
			dFValorFiltro.setDateFormat("dd/MM/yyyy HH:mm:ss");
			dFValorFiltro.setResolution(DateField.RESOLUTION_HOUR);
			dFValorFiltro.setResolution(DateField.RESOLUTION_MIN);
		}else{
			dFValorFiltro.setDateFormat("dd/MM/yyyy");
			
		}
		
		dFValorFiltro.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if(cbFiltro.getValue() != null){
					
					if(btAdd != null && dFValorFiltro.getValue() != null && !editing){
						btAdd.setEnabled(true);
						String idButton = String.valueOf(listaParametros.size())+dFValorFiltro.getValue().toString(); 
						btAdd.setId(idButton);
						btAdd.setEnabled(true);
					}
					
				}else{
					if(btAdd != null){
						btAdd.setEnabled(false);
					}
				}				
			}
		});
		
		return dFValorFiltro;
	}
	
	
	private ComboBox buildcbValorFiltro(String v, String coluna, final boolean editing) {	
		cBValorFiltro = new ComboBox();
		cBValorFiltro.setWidth("404px");
		cBValorFiltro.setImmediate(true);
		cBValorFiltro.setNullSelectionAllowed(false);
		cBValorFiltro.setTextInputAllowed(false);
		
				
		if(coluna.equals("Sexo")){
			cBValorFiltro.addItem("MASCULINO");
			cBValorFiltro.addItem("FEMININO");
		}else if(coluna.equals("Tipo Pessoa")){
			cBValorFiltro.addItem("Pessoa F??sica");
			cBValorFiltro.addItem("Pessoa Jur??dica");
		}else if(coluna.equals("Como nos Conheceu?")){
			
			
			List<ComoNosConheceu> comos = ComoNosConheceuDAO.getComoNosConheceu();
			
			for (ComoNosConheceu como : comos) {
				cBValorFiltro.addItem(como.getNome());
			}
			
			
		}else if(coluna.equals("Categoria")){
			
			List<Categorias> categorias = CategoriasDAO.getCategorias();
			
			for (Categorias categorias2 : categorias) {
				cBValorFiltro.addItem(categorias2.getNome());
			}
			
		}else if(coluna.equals("Status")){
			cBValorFiltro.addItem("ATIVO");
			cBValorFiltro.addItem("INATIVO");
		}
		
		
		 
		
		cBValorFiltro.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(cbFiltro.getValue() != null){
					
					if(btAdd != null && !editing){
						btAdd.setEnabled(true);
						String idButton = String.valueOf(listaParametros.size())+cBValorFiltro.getValue().toString(); 
						btAdd.setId(idButton);
						btAdd.setEnabled(true);
					}
					
				}else{
					if(btAdd != null){
						btAdd.setEnabled(false);
					}
				}
			}
			
			
		});	
		
		
		if(v != null){
			cBValorFiltro.select(v);
			cBValorFiltro.setReadOnly(true);
		}
				
		return cBValorFiltro;
	}
	
	private ComboBox buildCbOperador(String v) {
		cbOperador = new ComboBox();
		cbOperador.setNullSelectionAllowed(false);
		cbOperador.setTextInputAllowed(false);
		
		cbOperador.setImmediate(true);		
		cbOperador.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				valorOperador = event.getProperty().toString();
			}
		});
		
		if(v != null){
			cbOperador.addItem(v);
			cbOperador.select(v);
			cbOperador.setReadOnly(true);
		}
		
		return cbOperador;
	}
	private ComboBox buildCbFiltro(final String v) {
		cbFiltro = new ComboBox();
		cbFiltro.setTextInputAllowed(false);
//		cbFiltro.focus();
		cbFiltro.setNullSelectionAllowed(false);
		cbFiltro.addItem("C??digo");
		cbFiltro.addItem("Nome/Raz??o Social");
		cbFiltro.addItem("CPF/CNPJ");
		cbFiltro.addItem("Tipo Pessoa");
		cbFiltro.addItem("Inscri????o Estadual/RG");
		cbFiltro.addItem("Nome Fantasia");
		cbFiltro.addItem("Contato");
		cbFiltro.addItem("Sexo");
		cbFiltro.addItem("Data de Nascimento");
		cbFiltro.addItem("Telefone Principal"); // telefone1
		cbFiltro.addItem("Telefone Alternativo"); // telefone2
		cbFiltro.addItem("Telefone Alternativo 2"); // celular 1
		cbFiltro.addItem("Telefone Alternativo 3"); // celular 2
		cbFiltro.addItem("Email Principal");
		cbFiltro.addItem("Email Alternativo");
		cbFiltro.addItem("Como nos Conheceu?");
		cbFiltro.addItem("OBS");
		cbFiltro.addItem("Data de Cadastro");
		cbFiltro.addItem("Data de Altera????o");
		cbFiltro.addItem("Tratamento");
		cbFiltro.addItem("Nome Fantasia");
		cbFiltro.addItem("Contato");
		cbFiltro.addItem("Telefone Alternativo 2");
		cbFiltro.addItem("Telefone Alternativo 3");
		cbFiltro.addItem("Email Principal");
		cbFiltro.addItem("Email Alternativo");
		cbFiltro.addItem("Categoria");
		cbFiltro.addItem("Como Quer Ser Chamado");
		cbFiltro.addItem("Status");		
		cbFiltro.addItem("CEP");
		cbFiltro.addItem("Endere??o");
		cbFiltro.addItem("N??mero");
		cbFiltro.addItem("Bairro");
		cbFiltro.addItem("Cidade");
		cbFiltro.addItem("Pais");
		cbFiltro.addItem("Complemento");
		cbFiltro.addItem("Referencia");
		cbFiltro.addItem("Black List");
		cbFiltro.setImmediate(true);
		cbFiltro.setWidth("146px");
		
		
		cbFiltro.addValueChangeListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				
				if(v == null){
					String coluna = cbFiltro.getValue().toString();
					Class<?> type = container.getType(selectFiltro(coluna)); 				
					
					if(cbOperador != null){
						cbOperador.removeAllItems();
						if(type == String.class){					
							cbOperador.addItem("CONTEM");
							cbOperador.addItem("NAO CONTEM");
							cbOperador.addItem("IGUAL");
							cbOperador.addItem("DIFERENTE");
							
							cbOperador.select("CONTEM");
							
							if(coluna.equals("Sexo") || coluna.equals("Tipo Pessoa") || coluna.equals("Como nos Conheceu?") || coluna.equals("Categoria") || coluna.equals("Status")){
								if(hlFiltro.getComponentIndex(tfValorFiltro) > 0){
									hlFiltro.replaceComponent(tfValorFiltro, buildcbValorFiltro(v, coluna,false));
								}else if(hlFiltro.getComponentIndex(dFValorFiltro) > 0){
									hlFiltro.replaceComponent(dFValorFiltro, buildcbValorFiltro(v, coluna,false));
								}else{
									hlFiltro.replaceComponent(cBValorFiltro, buildcbValorFiltro(v, coluna,false));
								}
							}else{
								
								if(hlFiltro.getComponentIndex(cBValorFiltro) > 0){
									hlFiltro.replaceComponent(cBValorFiltro, buildTfValorFiltro(v,false));
								}else if(hlFiltro.getComponentIndex(dFValorFiltro) > 0){
									hlFiltro.replaceComponent(dFValorFiltro, buildTfValorFiltro(v,false));
								}else{
									hlFiltro.replaceComponent(tfValorFiltro, buildTfValorFiltro(v,false));
								}
							}
							
							
						}else if(type == Integer.class){
							cbOperador.addItem("IGUAL");
							cbOperador.addItem("DIFERENTE");
							cbOperador.addItem("MAIOR QUE");
							cbOperador.addItem("MENOR QUE");
							cbOperador.addItem("MAIOR IGUAL QUE");
							cbOperador.addItem("MENOR IGUAL QUE");
							
							
							cbOperador.select("IGUAL");
							
							if(hlFiltro.getComponentIndex(cBValorFiltro) > 0){
								hlFiltro.replaceComponent(cBValorFiltro, buildTfValorFiltro(v,false));
							}else if(hlFiltro.getComponentIndex(dFValorFiltro) > 0){
								hlFiltro.replaceComponent(dFValorFiltro, buildTfValorFiltro(v,false));
							}else{
								hlFiltro.replaceComponent(tfValorFiltro, buildTfValorFiltro(v,false));
							}
						}else if(type == Date.class){
							cbOperador.addItem("IGUAL");
							cbOperador.addItem("DIFERENTE");
							cbOperador.addItem("MAIOR QUE");
							cbOperador.addItem("MENOR QUE");
							cbOperador.addItem("MAIOR IGUAL QUE");
							cbOperador.addItem("MENOR IGUAL QUE");
							
							cbOperador.select("IGUAL");
							
							if(hlFiltro.getComponentIndex(cBValorFiltro) > 0){
								hlFiltro.replaceComponent(cBValorFiltro, buildDfValorFiltro(v, coluna,false));
							}else if(hlFiltro.getComponentIndex(dFValorFiltro) > 0){
								hlFiltro.replaceComponent(dFValorFiltro, buildDfValorFiltro(v, coluna,false));
							}else{
								hlFiltro.replaceComponent(tfValorFiltro, buildDfValorFiltro(v, coluna,false));								
							}
						}
						
						
					
					}
				}
			}
		});
		
		if(v != null){
			cbFiltro.select(selectHeader(v));
			cbFiltro.setReadOnly(true); 
		}

		
		
	
		return cbFiltro;
	}
	public String selectFiltro(String s) {
		
		String filtro = "";
		if(s.equals("Nome/Raz??o Social")){
			filtro = "nome_razao";			
		}else if(s.equals("C??digo")){
			filtro = "id";			
		}else if(s.equals("CPF/CNPJ")){
			filtro = "doc_cpf_cnpj";			
		}else if(s.equals("Inscri????o Estadual/RG")){
			filtro = "doc_rg_insc_estadual";			
		}else if(s.equals("Nome Fantasia")){
			filtro = "nome_fantasia";			
		}else if(s.equals("Sexo")){
			filtro = "sexo";			
		}else if(s.equals("Data de Nascimento")){
			filtro = "data_nascimento";			
		}else if(s.equals("Telefone Principal")){
			filtro = "telefone1";			
		}else if(s.equals("Telefone Alternativo")){
			filtro = "telefone2";			
		}else if(s.equals("Tratamento")){
			filtro = "tratamento";			
		}else if(s.equals("Nome Fantasia")){
			filtro = "nome_fantasia";			
		}else if(s.equals("Contato")){
			filtro = "contato";			
		}else if(s.equals("Telefone Alternativo 2")){
			filtro = "celular1";			
		}else if(s.equals("Telefone Alternativo 3")){
			filtro = "celular2";			
		}else if(s.equals("Email Principal")){
			filtro = "email";			
		}else if(s.equals("Como nos Conheceu?")){
			filtro = "como_nos_conheceu.nome";			
		}else if(s.equals("Email Alternativo")){
			filtro = "msn";			
		}else if(s.equals("OBS")){
			filtro = "obs";			
		}else if(s.equals("Data de Cadastro")){
			filtro = "data_cadastro";			
		}else if(s.equals("Data de Altera????o")){
			filtro = "data_alteracao";			
		}else if(s.equals("Categoria")){
			filtro = "categoria.nome";			
		}else if(s.equals("Como Quer Ser Chamado")){
			filtro = "como_quer_ser_chamado";			
		}else if(s.equals("Status")){
			filtro = "status";			
		}else if(s.equals("CEP")){
			filtro = "endereco_principal.cep";			
		}else if(s.equals("Endere??o")){
			filtro = "endereco_principal.endereco";			
		}else if(s.equals("N??mero")){
			filtro = "endereco_principal.numero";			
		}else if(s.equals("Bairro")){
			filtro = "endereco_principal.bairro";			
		}else if(s.equals("Cidade")){
			filtro = "endereco_principal.cidade";			
		}else if(s.equals("Pais")){
			filtro = "endereco_principal.pais";			
		}else if(s.equals("Complemento")){
			filtro = "endereco_principal.complemento";			
		}else if(s.equals("Referencia")){
			filtro = "endereco_principal.referencia";			
		}else if(s.equals("Tipo Pessoa")){
			filtro = "tipo_pessoa";			
		}else if(s.equals("Black List")){
			filtro = "black_list";			
		}
				
		return filtro;
	}
	public String selectHeader(String s) {
		
		String filtro = "";
		if(s.equals("nome_razao")){
			filtro = "Nome/Raz??o Social";			
		}else if(s.equals("id")){
			filtro = "C??digo";			
		}else if(s.equals("doc_cpf_cnpj")){
			filtro = "CPF/CNPJ";			
		}else if(s.equals("doc_rg_insc_estadual")){
			filtro = "Inscri????o Estadual/RG";			
		}else if(s.equals("nome_fantasia")){
			filtro = "Nome Fantasia";			
		}else if(s.equals("sexo")){
			filtro = "Sexo";			
		}else if(s.equals("data_nascimento")){
			filtro = "Data de Nascimento";			
		}else if(s.equals("telefone1")){
			filtro = "Telefone Principal";			
		}else if(s.equals("telefone2")){
			filtro = "Telefone Alternativo";			
		}else if(s.equals("tratamento")){
			filtro = "Tratamento";			
		}else if(s.equals("nome_fantasia")){
			filtro = "Nome Fantasia";			
		}else if(s.equals("contato")){
			filtro = "Contato";			
		}else if(s.equals("celular1")){
			filtro = "Telefone Alternativo 2";			
		}else if(s.equals("celular2")){
			filtro = "Telefone Alternativo 3";			
		}else if(s.equals("email")){
			filtro = "Email Principal";			
		}else if(s.equals("como_nos_conheceu.nome")){
			filtro = "Como nos Conheceu?";			
		}else if(s.equals("msn")){
			filtro = "Email Alternativo";			
		}else if(s.equals("obs")){
			filtro = "OBS";			
		}else if(s.equals("data_cadastro")){
			filtro = "Data de Cadastro";			
		}else if(s.equals("data_alteracao")){
			filtro = "Data de Altera????o";			
		}else if(s.equals("categoria.nome")){
			filtro = "Categoria";			
		}else if(s.equals("como_quer_ser_chamado")){
			filtro = "Como Quer Ser Chamado";			
		}else if(s.equals("status")){
			filtro = "Status";			
		}else if(s.equals("endereco_principal.cep")){
			filtro = "CEP";			
		}else if(s.equals("endereco_principal.endereco")){
			filtro = "Endere??o";			
		}else if(s.equals("endereco_principal.numero")){
			filtro = "N??mero";			
		}else if(s.equals("endereco_principal.bairro")){
			filtro = "Bairro";			
		}else if(s.equals("endereco_principal.cidade")){
			filtro = "Cidade";			
		}else if(s.equals("endereco_principal.pais")){
			filtro = "Pais";			
		}else if(s.equals("endereco_principal.complemento")){
			filtro = "Complemento";			
		}else if(s.equals("endereco_principal.referencia")){
			filtro = "Referencia";			
		}else if(s.equals("tipo_pessoa")){
			filtro = "Tipo Pessoa";			
		}else if(s.equals("black_list")){
			filtro = "Black List";			
		}
				
		return filtro;
	}
	private Button buildBtAdd(boolean editing, String id) {
		btAdd = new Button("Add",new Button.ClickListener() {
					
				@Override
				public void buttonClick(ClickEvent event) {
					hlFiltro = new HorizontalLayout();
					
					if(event.getButton().getCaption().toString().equals("Add")){

						if(cbFiltro.getValue().toString().equals("Sexo") || cbFiltro.getValue().toString().equals("Tipo Pessoa") || cbFiltro.getValue().toString().equals("Como nos Conheceu?") || cbFiltro.getValue().toString().equals("Categoria") || cbFiltro.getValue().toString().equals("Status")){
							cbFiltro.setEnabled(false);
							cbOperador.setEnabled(false);
							cBValorFiltro.setEnabled(false);
														
							String idSearch = String.valueOf(listaParametros.size())+cBValorFiltro.getValue().toString();
							listaParametros.add(new SearchParameters(idSearch, selectFiltro(cbFiltro.getValue().toString()), cbOperador.getValue().toString(), cBValorFiltro.getValue().toString()));
							
							btAdd.setCaption("Remover");
							hlFiltro.addComponent(buildCbFiltro(null));
							hlFiltro.addComponent(buildCbOperador(null));
							hlFiltro.addComponent(buildTfValorFiltro(null,false));
							hlFiltro.addComponent(buildBtAdd(false, null));
							vlFiltros.addComponent(hlFiltro);		
						}else if(cbFiltro.getValue().toString().equals("Data de Cadastro") ||  cbFiltro.getValue().toString().equals("Data de Altera????o") ||  cbFiltro.getValue().toString().equals("Data de Nascimento")){
							cbFiltro.setEnabled(false);
							cbOperador.setEnabled(false);
							dFValorFiltro.setEnabled(false);
														
							String idSearch = String.valueOf(listaParametros.size())+dFValorFiltro.getValue().toString();
							
							if(!cbFiltro.getValue().toString().equals("Data de Nascimento")){
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
								listaParametros.add(new SearchParameters(idSearch, selectFiltro(cbFiltro.getValue().toString()), cbOperador.getValue().toString(), sdf.format(dFValorFiltro.getValue())));
							}else{
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								listaParametros.add(new SearchParameters(idSearch, selectFiltro(cbFiltro.getValue().toString()), cbOperador.getValue().toString(), sdf.format(dFValorFiltro.getValue())));
							}
							
							btAdd.setCaption("Remover");
							hlFiltro.addComponent(buildCbFiltro(null));
							hlFiltro.addComponent(buildCbOperador(null));
							hlFiltro.addComponent(buildTfValorFiltro(null,false));
							hlFiltro.addComponent(buildBtAdd(false, null));
							vlFiltros.addComponent(hlFiltro);		
						}else{
							if(tfValorFiltro.getValue() != null && !tfValorFiltro.getValue().toString().equals("") && !tfValorFiltro.getValue().toString().isEmpty()){
								
								
								String coluna = cbFiltro.getValue().toString();
								
								
								cbFiltro.setEnabled(false);
								cbOperador.setEnabled(false);
								tfValorFiltro.setEnabled(false);
																
								String idSearch = String.valueOf(listaParametros.size())+tfValorFiltro.getValue();
								listaParametros.add(new SearchParameters(idSearch, selectFiltro(cbFiltro.getValue().toString()), cbOperador.getValue().toString(), tfValorFiltro.getValue()));
									
								btAdd.setCaption("Remover");
									
								hlFiltro.addComponent(buildCbFiltro(null));
								hlFiltro.addComponent(buildCbOperador(null));
								hlFiltro.addComponent(buildTfValorFiltro(null,false));
								hlFiltro.addComponent(buildBtAdd(false, null));
								vlFiltros.addComponent(hlFiltro);
								
							}
						}
						
						
					}else{
						
						Integer i = 0;	
						for(SearchParameters sp: listaParametros){
							i++;
							if(sp.getId().equals(event.getButton().getId())){
								listaParametros.remove(i-1);
								break;
							}
						}
						
						
						hlFiltro = new HorizontalLayout();
						hlFiltro.addComponent(buildCbFiltro(null));
						hlFiltro.addComponent(buildCbOperador(null));
						hlFiltro.addComponent(buildTfValorFiltro(null,false));
						hlFiltro.addComponent(buildBtAdd(false, null));						
                        						
							
						vlFiltros.removeComponent(event.getButton().getParent());
						//vlFiltros.replaceComponent(event.getButton().getParent(), hlFiltro);
						
						
						for (int j = 0; j < vlFiltros.getComponentCount(); j++) {
							
						
							Object c = vlFiltros.getComponent(j);							
							
							if(vlFiltros.getComponentIndex((Component) c) == vlFiltros.getComponentCount()-1){
								vlFiltros.replaceComponent((Component) c, hlFiltro);
								break;
							}
						}
						

						
						
					}
					
					fireEvent(new AddFiltroEvent(getUI(), listaParametros));
				}
				
			});
		btAdd.setEnabled(false);
		btAdd.setStyleName(Reindeer.BUTTON_SMALL);
		
		if(editing){
			btAdd.setEnabled(true);
			btAdd.setId(id);
			btAdd.setCaption("Remover");
		}
		
		return btAdd;
	}
		
	public Button buildBtSalvar() {
		
		btSalvar = new Button("OK", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				if(cbOrdenacao.isValid() && cbTipoTabela.isValid() && cbResumo.isValid() && listaParametros.size() > 0){
					fireEvent(new RelatorioClienteEvent(getUI(), true, listaParametros, cbTipoTabela.getValue().toString(), cbOrdenacao.getValue().toString(), cbOrientacao.getValue().toString(), cbResumo.getValue().toString()));		
					close();
				}else{
					
						if(cbFiltro.getValue()==null || cbFiltro.getValue().equals("")){
							cbFiltro.addStyleName("invalid-txt");
						}else{
							cbFiltro.removeStyleName("invalid-txt");
						}
						if(cbOperador.getValue()==null || cbOperador.getValue().equals("")){
							cbOperador.addStyleName("invalid-txt");
						}else{
							cbOperador.removeStyleName("invalid-txt");
						}
						if(tfValorFiltro.getValue()==null || tfValorFiltro.getValue().equals("")){
							tfValorFiltro.addStyleName("invalid-txt");
						}else{
							tfValorFiltro.removeStyleName("invalid-txt");
						}
											
						if(!cbOrdenacao.isValid()){
							cbOrdenacao.addStyleName("invalid-txt");
						}else{
							cbOrdenacao.removeStyleName("invalid-txt");
						}
						if(!cbTipoTabela.isValid()){
							cbTipoTabela.addStyleName("invalid-txt");
						}else{
							cbTipoTabela.removeStyleName("invalid-txt");
						}
						if(!cbOrientacao.isValid()){
							cbOrientacao.addStyleName("invalid-txt");
						}else{
							cbOrientacao.removeStyleName("invalid-txt");
						}
						if(!cbResumo.isValid()){
							cbResumo.addStyleName("invalid-txt");
						}else{
							cbResumo.removeStyleName("invalid-txt");
						}
						
				
					
					Notify.Show("N??o ?? Possivel Gerar Relat??rio, Verifique os Campos Obrigat??rios", Notify.TYPE_ERROR);
				}
			}
		});
		

		ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.ENTER,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		
		btSalvar.addShortcutListener(slbtOK);
		
		btSalvar.setStyleName("default");
		return btSalvar;
	}

	Button btCancelar;
	public Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				fireEvent(new RelatorioClienteEvent(getUI(), false, null,null,null, null, null));
				close();	
			}
		});
		
		ShortcutListener slbtCancelar = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		
		btCancelar.addShortcutListener(slbtCancelar);
		
		
		return btCancelar;
	}
	
	
	public void addListerner(RelatorioClienteListerner target){
		try {
			Method method = RelatorioClienteListerner.class.getDeclaredMethod("onClose", RelatorioClienteEvent.class);
			addListener(RelatorioClienteEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("M??todo n??o Encontrado!");
		}
	}
	public void removeListerner(RelatorioClienteListerner target){
		removeListener(RelatorioClienteEvent.class, target);
	}
	
	
	public void addListerner(AddFiltroListerner target){
		try {
			Method method = AddFiltroListerner.class.getDeclaredMethod("onClose", AddFiltroEvent.class);
			addListener(AddFiltroEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("M??todo n??o Encontrado!");
		}
	}
	public void removeListerner(AddFiltroListerner target){
		removeListener(AddFiltroEvent.class, target);
	}
	
	
	public static class AddFiltroEvent extends Event{

		private List<SearchParameters> filtros;
		
		
		public AddFiltroEvent(Component source, List<SearchParameters> filtros) {
			super(source);
			
			
			this.filtros = filtros;
		}
		
		public List<SearchParameters> getFiltros(){
			return filtros;
		}
		
		
		
	}
	public interface AddFiltroListerner extends Serializable{
		public void onClose(AddFiltroEvent event);
	}
	
	
	public static class RelatorioClienteEvent extends Event{
		
		
		private boolean confirm;
		private List<SearchParameters> parametros;
		private String tipo;
		private String ordenacao;
		private String orientacao;
		private String resumo;
		
		public RelatorioClienteEvent(Component source, boolean confirm, List<SearchParameters> parametros, String tipo, String ordenacao, 
				String orientacao, String resumo) {
			super(source);
			this.confirm = confirm;
			this.parametros =  parametros;
			this.tipo = tipo;
			this.ordenacao = ordenacao;
			this.orientacao = orientacao;
			this.resumo = resumo;
		}

		public String getResumo(){
			return resumo;
		}
		
		public String getOrientacao(){
			return orientacao;
		}
		
		public List<SearchParameters> getParametros() {
			return parametros;
		}	
		
		public String getTipo(){
			return tipo;
		}
		
		public String getOrdenacao(){
			return ordenacao;
		}

		public boolean isConfirm() {
			return confirm;
		}	
		
		
	}
	public interface RelatorioClienteListerner extends Serializable{
		public void onClose(RelatorioClienteEvent event);
	}

	
	
	public void adicionarFiltro(){
				
		for(SearchParameters sp:listaParametros){
			
				if(sp.getOperador().equals("IGUAL")){
					
					if(container.getType(sp.getCampo()) == String.class){
						
						container.addContainerFilter(new Like(sp.getCampo(), sp.getValor(), true));
						
					}else if(container.getType(sp.getCampo()) == Date.class){
						String date = sp.getValor();
						Date dtValor = new Date(Date.parse(date.substring(3, 6)+date.substring(0, 3)+date.substring(6,10)));
						container.addContainerFilter(new Equal(sp.getCampo(), dtValor));
						
					}else if(container.getType(sp.getCampo()) == Integer.class){
												
						container.addContainerFilter(new Equal(sp.getCampo(), Integer.parseInt(sp.getValor())));
					}
					
				}else if(sp.getOperador().equals("DIFERENTE")){
					
					if(container.getType(sp.getCampo()) == String.class){
						container.addContainerFilter(Filters.not(Filters.eq(sp.getCampo(), sp.getValor(), true)));						
						
						
					}else if(container.getType(sp.getCampo()) == Date.class){
						String date = sp.getValor();
						Date dtValor = new Date(Date.parse(date.substring(3, 6)+date.substring(0, 3)+date.substring(6,10)));
						container.addContainerFilter(Filters.not(Filters.eq(sp.getCampo(), dtValor)));
						
					}else if(container.getType(sp.getCampo()) == Integer.class){
												
						//container.addContainerFilter(new Equal(sp.getCampo(), Integer.parseInt(sp.getValor())));
						container.addContainerFilter(Filters.not(Filters.eq(sp.getCampo(), Integer.parseInt(sp.getValor()))));
					}
					
				}else if(sp.getOperador().equals("CONTEM")){
					
					if(container.getType(sp.getCampo()) == String.class){
						container.addContainerFilter(sp.getCampo(), "%"+sp.getValor()+"%", false, false);
					}
					
				}else if(sp.getOperador().equals("NAO CONTEM")){
					
					if(container.getType(sp.getCampo()) == String.class){
						container.addContainerFilter(Filters.not(Filters.like(sp.getCampo(),"%"+sp.getValor()+"%", true)));			
					}
					
				}else if(sp.getOperador().equals("MAIOR QUE")){
					
					if(container.getType(sp.getCampo()) == Date.class && !sp.getCampo().equals("data_nascimento")){
						
						try {
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
							Date dtValor = sdf.parse(date);
							container.addContainerFilter(new Greater(sp.getCampo(), dtValor));
							
						}catch (Exception e) {
						
							e.printStackTrace();
						}					
					}
					
					if(container.getType(sp.getCampo()) == Date.class && sp.getCampo().equals("data_nascimento")){
						
						try {
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
							Date dtValor = sdf.parse(date);
							container.addContainerFilter(new Greater(sp.getCampo(), dtValor));
							
						}catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}					
					}
					
					if(container.getType(sp.getCampo()) == Integer.class){
						container.addContainerFilter(new Greater(sp.getCampo(), Integer.parseInt(sp.getValor())));					
					}
					
				}else if(sp.getOperador().equals("MENOR QUE")){
					
					if(container.getType(sp.getCampo()) == Date.class && !sp.getCampo().equals("data_nascimento")){
						
						try {
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
							Date dtValor = sdf.parse(date);						
							container.addContainerFilter(new Less(sp.getCampo(), dtValor));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					if(container.getType(sp.getCampo()) == Date.class && sp.getCampo().equals("data_nascimento")){
						
						try {
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
							Date dtValor = sdf.parse(date);						
							container.addContainerFilter(new Less(sp.getCampo(), dtValor));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					if(container.getType(sp.getCampo()) == Integer.class){
						container.addContainerFilter(new Less(sp.getCampo(), Integer.parseInt(sp.getValor())));					
					}
				}else if(sp.getOperador().equals("MAIOR IGUAL QUE")){
					if(container.getType(sp.getCampo()) == Date.class && !sp.getCampo().equals("data_nascimento")){
						
						try {
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
							Date dtValor = sdf.parse(date);
							container.addContainerFilter(new GreaterOrEqual(sp.getCampo(), dtValor));
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
					}
					
					if(container.getType(sp.getCampo()) == Date.class && sp.getCampo().equals("data_nascimento")){
						
						try {
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
							Date dtValor = sdf.parse(date);
							container.addContainerFilter(new GreaterOrEqual(sp.getCampo(), dtValor));
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
					}
					
					if(container.getType(sp.getCampo()) == Integer.class){
						container.addContainerFilter(new GreaterOrEqual(sp.getCampo(), Integer.parseInt(sp.getValor())));					
					}
				}else if(sp.getOperador().equals("MENOR IGUAL QUE")){
					
					if(container.getType(sp.getCampo()) == Date.class && !sp.getCampo().equals("data_nascimento")){
						
						
						try {
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");							
							Date dtValor = sdf.parse(date);
							container.addContainerFilter(new LessOrEqual(sp.getCampo(), dtValor));						
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
					if(container.getType(sp.getCampo()) == Date.class && sp.getCampo().equals("data_nascimento")){
						
						
						try {
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");							
							Date dtValor = sdf.parse(date);
							container.addContainerFilter(new LessOrEqual(sp.getCampo(), dtValor));						
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
					if(container.getType(sp.getCampo()) == Integer.class){
						container.addContainerFilter(new LessOrEqual(sp.getCampo(), Integer.parseInt(sp.getValor())));					
					}
				}
		}		
		
				
	}
}
