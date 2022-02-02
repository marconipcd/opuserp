package com.digital.opuserp.view.modulos.relatorio.Roterizacao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.EntityType;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.dao.RelatorioDAO;
import com.digital.opuserp.domain.Ose;
import com.digital.opuserp.domain.RelatorioPre;
import com.digital.opuserp.domain.RelatorioPreColunas;
import com.digital.opuserp.domain.RelatorioPreFiltro;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.exporterpdf.ExcelExporter;
import com.digital.opuserp.view.modulos.relatorio.SearchParameters;
import com.digital.opuserp.view.modulos.relatorio.Roterizacao.NovoRelatorioOse.AddFiltroOseEvent;
import com.digital.opuserp.view.modulos.relatorio.Roterizacao.NovoRelatorioOse.RelatorioOseEvent;
import com.digital.opuserp.view.modulos.relatorio.Roterizacao.SalvarRelatorioOseEditor.SalvarRelatorioOseEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Cursor;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.Series;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Property;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.UnsupportedFilterException;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Compare.Greater;
import com.vaadin.data.util.filter.Compare.GreaterOrEqual;
import com.vaadin.data.util.filter.Compare.Less;
import com.vaadin.data.util.filter.Compare.LessOrEqual;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.BaseTheme;

public class RelatorioOseRelRepInsView extends VerticalLayout{
	
	private JPAContainer<Ose> container;	
	private JPAContainer<RelatorioPre> relatoriosContainer;	
	private Table tb;
	private TextField tfNomeRelatorio;	
//	private Button btNovo;
//	private Button btEditar;Obuild
//	private Button btSalvar;
//	private Button btCarregar;
	private Button btImprimir;
	private Button btGerarChart;
	private HorizontalLayout hlFloat;	
	private Label lbRegistros;	
	private ShortcutListener slNovo;
	private ShortcutListener slImprimir;	
	private Integer codSubModulo;
	private GerenciarModuloDAO gmDAO;
	private List<SearchParameters> listaParametros = new ArrayList<SearchParameters>();
	private String tipo;
	private String ordenacao;
	private String orientacao;
	private String resumo;
	private String nomeRelatorio;
	private Table tbRelatorios;
	
	Window winRelatorios;
	
	private RelatorioPre relatorioPre;
	private List<RelatorioPreFiltro> filtrosRelatorio;
	private List<RelatorioPreColunas> colunasVisiveis;
	
	DateField dfData1;
	DateField dfData2;
	
	public RelatorioOseRelRepInsView(boolean act){		
		
		if(act){
			setSizeFull();
			gmDAO = new GerenciarModuloDAO();
			
			HorizontalLayout hlButons = new HorizontalLayout();
//			hlButons.addComponent(BuildbtNovo());
//			hlButons.addComponent(BuildbtEditar());
//			hlButons.addComponent(BuildbtSalvar());
//			hlButons.addComponent(BuildbtCarregar());
			
			
			addComponent(hlButons);
			setComponentAlignment(hlButons, Alignment.TOP_RIGHT);
			
			addComponent(new HorizontalLayout(){
				{
					setWidth("100%");
					
					dfData1 = new DateField();
					dfData1.setValue(new Date());
					
					dfData2 = new DateField();
					dfData2.setValue(new Date());
					
					addComponent(dfData1);
					addComponent(dfData2);
					
					addComponent(buildTfbusca());
					setExpandRatio(tfNomeRelatorio, 1.0f);
				}
			});
			
			addComponent(buildTbGeneric());
			hlButons.addComponent(BuildbtImprimir());					
	//		hlButons.addComponent(BuildbtGerarChart());

			
			hlFloat = new HorizontalLayout();
			hlFloat.setWidth("100%");				
			//hlFloat.addComponent(lbRegistros());
			Label lbLegend = new Label("F2 - Novo Cadastro | ENTER - Imprimir");
			lbLegend.setWidth("300px");
			hlFloat.addComponent(lbLegend);
			//hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
			hlFloat.setComponentAlignment(lbLegend, Alignment.BOTTOM_RIGHT);

			addComponent(hlFloat);			
			setExpandRatio(tb, 1);
			
		}
	}
	
	public void refresh(){
		replaceComponent(tb, buildTbGeneric()); 	
		setExpandRatio(tb, 1);		
//		btSalvar.setEnabled(false);
//		btCarregar.setEnabled(false);
	}
	
	public void buildShortcurEvents(Component c){
		
		if(c instanceof RelatorioOseView)
		{
//			btNovo.addShortcutListener(buildShortCutNovo());
			btImprimir.addShortcutListener(buildShortCutImprimir());
		}else{
//			if(btNovo != null || slNovo != null){				
//				btNovo.removeShortcutListener(slNovo);
//			}
			
			if(btImprimir != null || slImprimir != null){
				btImprimir.removeShortcutListener(slImprimir);			
			}
		}
	}
	
//	public Label lbRegistros(){
//		//lbRegistros = new Label(String.valueOf(container.size()) + " Registros Encontrados");
//		//return lbRegistros;
//	}
	
	public ShortcutListener buildShortCutNovo(){
		slNovo = new ShortcutListener("Novo",ShortcutAction.KeyCode.F2,null) {
			
			
			public void handleAction(Object sender, Object target) {
//				btNovo.click();
			}
		};
		return slNovo;
	}
	
	public ShortcutListener buildShortCutImprimir(){
		slImprimir = new ShortcutListener("Imprimir",ShortcutAction.KeyCode.ENTER,null) {
			
			
			public void handleAction(Object sender, Object target) {
				btImprimir.click();
			}
		};
		return slImprimir;
	}

	public JPAContainer<Ose> buildContainer(){
		container = JPAContainerFactory.makeBatchable(Ose.class,ConnUtil.getEntity());
		container.setAutoCommit(false);
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));		
		container.addNestedContainerProperty("cliente.nome_razao");
		container.addNestedContainerProperty("subgrupo.nome");
		container.addNestedContainerProperty("grupo.nome");
		container.addNestedContainerProperty("tipo_subgrupo.nome");
		container.addNestedContainerProperty("veiculo_id.cod_veiculo");
		
		container.addContainerFilter(Filters.not(Filters.eq("subgrupo", null)));
		container.addContainerFilter(Filters.not(Filters.eq("grupo", null)));
		container.addContainerFilter(Filters.not(Filters.eq("tipo_subgrupo", null)));

			
		return container;
	}

	
	public Table buildTbGeneric() {
		tb = new Table(null){
			
//			@Override
//			protected String formatPropertyValue(Object rowId, Object colId,
//					Property<?> property) {
//				
//				if(tb.getType(colId).equals(Date.class)){
//					
//					if(colId.equals("data_encaminhamento")){
//						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//						if(tb.getItem(rowId).getItemProperty(colId).getValue() != null){
//							return sdf.format((Date)tb.getItem(rowId).getItemProperty(colId).getValue());					
//						}else{
//							return super.formatPropertyValue(rowId, colId, property);	
//							}
//						
//					}else if(colId.equals("data_ex")){
//						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//						if(tb.getItem(rowId).getItemProperty(colId).getValue() != null){
//							return sdf.format((Date)tb.getItem(rowId).getItemProperty(colId).getValue());					
//						}else{
//							return super.formatPropertyValue(rowId, colId, property);	
//						}
//						
//					}else if(colId.equals("data_abertura")){
//						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//						if(tb.getItem(rowId).getItemProperty(colId).getValue() != null){
//							return sdf.format((Date)tb.getItem(rowId).getItemProperty(colId).getValue());					
//						}else{
//							return super.formatPropertyValue(rowId, colId, property);	
//						}
//						
//					}else if(colId.equals("data_conclusao")){
//						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//						if(tb.getItem(rowId).getItemProperty(colId).getValue() != null){
//							return sdf.format((Date)tb.getItem(rowId).getItemProperty(colId).getValue());					
//						}else{
//							return super.formatPropertyValue(rowId, colId, property);	
//						}
//						
//					}else if(colId.equals("data_fechamento")){
//						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//						if(tb.getItem(rowId).getItemProperty(colId).getValue() != null){
//							return sdf.format((Date)tb.getItem(rowId).getItemProperty(colId).getValue());					
//						}else{
//							return super.formatPropertyValue(rowId, colId, property);	
//						}
//						
//					}else{
//						if(tb.getItem(rowId).getItemProperty(colId).getValue() != null){
//							SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");						
//							return sdf.format(tb.getItem(rowId).getItemProperty(colId).getValue());
//						}else{
//							return super.formatPropertyValue(rowId, colId, property);	
//						}				
//					}
//				}
//				
//				if(colId.equals("id")){
//					
//					if(tb.getItem(rowId).getItemProperty("id").getValue() != null){						
//						return ((Integer)tb.getItem(rowId).getItemProperty("id").getValue()).toString();				
//					}					
//				}	
//				
//				return super.formatPropertyValue(rowId, colId, property);
//				
//			}
		};		
		tb.setSizeFull();
		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		
		tb.addContainerProperty("Contrato", String.class, "");
		tb.addContainerProperty("Cliente", String.class, "");
		
		EntityManager em  = ConnUtil.getEntity();
		Query q = em.createNativeQuery("SELECT o.ACESSO_CLIENTE_ID, c.NOME_RAZAO  FROM ose o, clientes c WHERE o.CLIENTES_ID = c.ID AND o.GRUPO_ID = 2 AND "
				+ "`DATA_EX` >= '2019-01-01 00:00:00' AND `DATA_EX` <= '2019-07-31 00:00:00' AND o.STATUS_2 LIKE 'FECHADO' AND "
				+ "EXISTS(SELECT ose.ACESSO_CLIENTE_ID FROM ose WHERE ose.ACESSO_CLIENTE_ID = o.ACESSO_CLIENTE_ID AND ose.DATA_EX > o.DATA_EX "
				+ "AND ose.GRUPO_ID = 9)");
		
		//q.setParameter("dt1", dfData1.getValue());
		//q.setParameter("dt2", dfData2.getValue());
		
		if(q.getResultList().size() > 0){
			
			List<Object> resultados = q.getResultList();
			for (Object o : resultados) {
				System.out.println(o);
				//tb.addItem(new Object[]{}, tb.getItemIds().size()+1);
			}
			
		}
		
		
//		tb.setVisibleColumns(new Object[] {"id","cliente.nome_razao","grupo.nome","subgrupo.nome",
//				"tipo_subgrupo.nome","veiculo_id.cod_veiculo",
//		"endereco","cidade","bairro",
//		"data_ex","data_encaminhamento","data_abertura","data_conclusao","data_fechamento","tempo_atendimento","turno","base","concentrador","plano","material","operador_abertura",
//		"contato","status","problema","uso_interno","conclusao","tipo_servico","tecnico","operador"});

		
	
		tb.setColumnCollapsingAllowed(true);
		
		//tb.setConverter("id", null);

		
		tb.setImmediate(true);
		
//		tb.setColumnCollapsible("cliente.nome_razao", false);
		
		
		tb.setImmediate(true);
		tb.addValueChangeListener(new Property.ValueChangeListener() {
			
			
			public void valueChange(ValueChangeEvent event) {
					
				tb.removeStyleName("corrige-tamanho-table");
				tb.addStyleName("corrige-tamanho-table");
			}
		});
		
//		tb.sort(new Object[]{"cliente.nome_razao"}, new boolean[]{true});

		
		return tb;
	}

	public TextField buildTfbusca() {
		tfNomeRelatorio = new TextField();
		tfNomeRelatorio.setWidth("100%");
		tfNomeRelatorio.addStyleName("align-center");
		tfNomeRelatorio.addStyleName("bold");
		tfNomeRelatorio.setValue("");
		tfNomeRelatorio.setReadOnly(true);

		return tfNomeRelatorio;
	}

	
	public void addFilter(Integer s) {
		// TODO Auto-generated method stub
		
	}

	
	public void addFilter(String s) {
		relatoriosContainer.removeAllContainerFilters();
		relatoriosContainer.addContainerFilter(Filters.eq("codEmpresa", OpusERP4UI.getEmpresa().getId()));
		relatoriosContainer.addContainerFilter(Filters.eq("codUsuario", OpusERP4UI.getUsuarioLogadoUI()));
		relatoriosContainer.addContainerFilter(Filters.eq("codSubModulo", getCodSubModulo()));
		relatoriosContainer.sort(new Object[]{"data_cadastro"}, new boolean[]{false});
		relatoriosContainer.addNestedContainerProperty("codUsuario.username");	
		
		Object[] collums = tbRelatorios.getVisibleColumns();		
		List<Filter> filtros = new ArrayList<Filter>();		
		
		for(Object c:collums){		 

			
			
			if(!c.toString().equals("x")){					   		
				if(relatoriosContainer.getType(c.toString()) == String.class){					   	
					filtros.add(new Like(c.toString(), "%"+s+"%", false));
				}			
			}			
		}
		
		relatoriosContainer.addContainerFilter(Filters.or(filtros));
		relatoriosContainer.applyFilters();
//		hlFloat.replaceComponent(lbRegistros, buildLbRegistros());
	}
	
	private void loadRelatorioColuna(List<RelatorioPreColunas> colunas){
		
		for (Object ob : tb.getVisibleColumns()) {
			tb.setColumnCollapsible(ob.toString(), true);
			tb.setColumnCollapsed(ob.toString(), true);
		}
		
		
		for (RelatorioPreColunas relatorioPreColunas : colunas) {
			tb.setColumnCollapsible(relatorioPreColunas.getColuna(), true);
			tb.setColumnCollapsed(relatorioPreColunas.getColuna(), false);
		}	
		
	}
	
	private void loadRelatorio(List<RelatorioPreFiltro> filtros){
		
		try{
		
			listaParametros = new ArrayList<>();
			Integer i = 1;
			for (RelatorioPreFiltro filtro : filtros) {
				
				
				String idSearch = String.valueOf(listaParametros.size())+filtro.getValor();
				listaParametros.add(new SearchParameters(idSearch, filtro.getColuna(), filtro.getOperador(), filtro.getValor()));
														
				adicionarFiltro();

				i++;
			}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public String selectFiltro(String s) {
		
		String filtro = "";

		if(s.equals("Cliente")){
			filtro = "cliente.nome_razao";							
		}else if(s.equals("Código")){
			filtro = "id";					
		}else if(s.equals("SubGrupo")){
			filtro = "subgrupo.nome";					
		}else if(s.equals("Grupo")){
			filtro = "grupo.nome";					
		}else if(s.equals("Tipo SubGrupo")){
			filtro = "tipo_subgrupo.nome";				
		}else if(s.equals("Contato")){
			filtro = "contato";					
		}else if(s.equals("Status")){
			filtro = "status";					
		}else if(s.equals("Data Previsão")){
			filtro = "data_ex";	
		}else if(s.equals("Data Encaminhado")){
			filtro = "data_encaminhamento";	
		}else if(s.equals("Data Abertura")){
			filtro = "data_abertura";	
		}else if(s.equals("Data Conclusão")){
			filtro = "data_conclusao";			
		}else if(s.equals("Data Fechamento")){
			filtro = "data_fechamento";			
		}else if(s.equals("Data Inicio Tratamento")){
			filtro = "data_inicio_tratamento";					
		}else if(s.equals("Tempo Total Tratamento")){
			filtro = "tempo_total_tratamento";					
		}else if(s.equals("Motivo Reagendamento")){
			filtro = "motivo_reagendamento";					
		}else if(s.equals("Operador")){
			filtro = "operador";							
		}else if(s.equals("Conclusão")){
			filtro = "conclusao";		
		}else if(s.equals("Técnico")){
			filtro = "tecnico";			
		}else if(s.equals("Serviço")){
			filtro = "tipo_servico";		
		}else if(s.equals("Problema")){
			filtro = "problema";		
		}else if(s.equals("Uso Interno")){
			filtro = "uso_interno";
		}else if(s.equals("Veiculo")){
			filtro = "veiculo_id.cod_veiculo";		
		}else if(s.equals("Operador Abertura")){
			filtro = "operador_abertura";		
		}else if(s.equals("Material")){
			filtro = "material";		
		}else if(s.equals("Plano")){
			filtro = "plano";		
		}else if(s.equals("Concentrador")){
			filtro = "concentrador";			
		}else if(s.equals("Base")){
			filtro = "base";	
		}else if(s.equals("Turno")){
			filtro = "turno";		
		}else if(s.equals("Endereço")){
			filtro = "endereco";					
		}else if(s.equals("Bairro")){
			filtro = "bairro";			
		}else if(s.equals("Cidade")){
			filtro = "cidade";			
		}else if(s.equals("Tempo de Atendimento")){
			filtro = "tempo_atendimento";			
		}	
		
		
		return filtro;
		
	}
	
	public boolean adicionarFiltro() throws UnsupportedFilterException, ParseException{
		boolean check = true;
		container.removeAllContainerFilters();
		container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
		
		if(ordenacao != null){
			container.sort(new Object[]{selectFiltro(ordenacao)}, new boolean[]{true});
		}
					
		
		for(SearchParameters sp:listaParametros){
			
				if(sp.getOperador().equals("IGUAL")){

					
					if(container.getType(sp.getCampo()) == String.class){
						
						
						check = true;
						
						if(sp.getCampo().equals("veiculo_id.cod_veiculo")||sp.getCampo().equals("cliente.nome_razao")||sp.getCampo().equals("subgrupo.nome")||sp.getCampo().equals("grupo.nome")|| sp.getCampo().equals("tipo_subgrupo.nome")){							
							container.addContainerFilter(new Like(sp.getCampo(), sp.getValor(), false));					
						}else{
							container.addContainerFilter(new Equal(sp.getCampo(),(sp.getValor())));
						}
	
					}else if(container.getType(sp.getCampo()) == Date.class){
						
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						container.addContainerFilter(new Equal(sp.getCampo(), sdf.parse(sp.getValor())));
						
					}else if(container.getType(sp.getCampo()) == Integer.class){
												
						container.addContainerFilter(new Equal(sp.getCampo(), Integer.parseInt(sp.getValor())));
					}
					
				}else if(sp.getOperador().equals("DIFERENTE")){
					
					if(container.getType(sp.getCampo()) == String.class){
						check = true;
							
						
						if(sp.getCampo().equals("veiculo_id.cod_veiculo")||sp.getCampo().equals("cliente.nome_razao")||sp.getCampo().equals("subgrupo.nome")||sp.getCampo().equals("grupo.nome")|| sp.getCampo().equals("tipo_subgrupo.nome")){							
							container.addContainerFilter(Filters.not(new Like(sp.getCampo(),sp.getValor(),false)));
						}
								
					}else if(container.getType(sp.getCampo()) == Date.class){
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						container.addContainerFilter(Filters.not(Filters.eq(sp.getCampo(), sdf.parseObject(sp.getValor()))));
						
					}else if(container.getType(sp.getCampo()) == Integer.class){
												
						//container.addContainerFilter(new Equal(sp.getCampo(), Integer.parseInt(sp.getValor())));
						container.addContainerFilter(Filters.not(Filters.eq(sp.getCampo(), Integer.parseInt(sp.getValor()))));
					}
					
				}else if(sp.getOperador().equals("CONTEM")){
					
					
					if(container.getType(sp.getCampo()) == String.class){
						check = true;
						container.addContainerFilter(new Like(sp.getCampo(),"%"+sp.getValor()+"%", false));
					}
					
				}else if(sp.getOperador().equals("NAO CONTEM")){
					
					if(container.getType(sp.getCampo()) == String.class){
						check = true;
											
						container.addContainerFilter(Filters.not(new Like(sp.getCampo(),"%"+sp.getValor()+"%", false)));			
					}
					
				}else if(sp.getOperador().equals("MAIOR QUE")){
					
										
					if(container.getType(sp.getCampo()) == Date.class){
						
						try {
							check = true;					
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							Date dtValor = sdf.parse(date);
							container.addContainerFilter(new Greater(sp.getCampo(), dtValor));
							
						}catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}					
					}
					
					if(container.getType(sp.getCampo()) == Integer.class){
						check = true;						
						container.addContainerFilter(new Greater(sp.getCampo(), Integer.parseInt(sp.getValor())));					
					}
					
				}else if(sp.getOperador().equals("MENOR QUE")){
					
										
					if(container.getType(sp.getCampo()) == Date.class){
						
						try {
							check = true;						
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							Date dtValor = sdf.parse(date);						
							container.addContainerFilter(new Less(sp.getCampo(), dtValor));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					if(container.getType(sp.getCampo()) == Integer.class){
						check = true;						
						container.addContainerFilter(new Less(sp.getCampo(), Integer.parseInt(sp.getValor())));					
					}
				}else if(sp.getOperador().equals("MAIOR IGUAL QUE")){
										
					if(container.getType(sp.getCampo()) == Date.class){
						
						try {
							check = true;						
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							Date dtValor = sdf.parse(date);
							container.addContainerFilter(new GreaterOrEqual(sp.getCampo(), dtValor));
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
					}
					
					if(container.getType(sp.getCampo()) == Integer.class){
						check = true;						
						container.addContainerFilter(new GreaterOrEqual(sp.getCampo(), Integer.parseInt(sp.getValor())));					
					}
				}else if(sp.getOperador().equals("MENOR IGUAL QUE")){
					
										
					if(container.getType(sp.getCampo()) == Date.class){
									
						try {
							check = true;							
							String date = sp.getValor();
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");							
							Date dtValor = sdf.parse(date);
							container.addContainerFilter(new LessOrEqual(sp.getCampo(), dtValor));						
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	
					}
					
					if(container.getType(sp.getCampo()) == Integer.class){
						check = true;						
						container.addContainerFilter(new LessOrEqual(sp.getCampo(), Integer.parseInt(sp.getValor())));					
					}
				}
		}		
		
		container.applyFilters();
		//hlFloat.replaceComponent(lbRegistros, lbRegistros());
		
		return check;
	}
	
//	public Button BuildbtEditar(){
//		btEditar = new Button("Editar", new Button.ClickListener() {
//			
//			@Override
//			public void buttonClick(ClickEvent event) {
//							
//				if(listaParametros != null && listaParametros.size() > 0){
//				
//						NovoRelatorioOse v = new NovoRelatorioOse("Editar Relatório Ose", true,tipo, orientacao, ordenacao,resumo, listaParametros );
//						v.addListerner(new NovoRelatorioOse.RelatorioOseListerner() {
//							
//							@Override
//							public void onClose(RelatorioOseEvent event) {
//								if(event.isConfirm()){
//									
//									try{
//										listaParametros = event.getParametros();
//										tipo = event.getTipo();
//										ordenacao = event.getOrdenacao();
//										orientacao = event.getOrientacao();
//										resumo = event.getResumo();
//										
//										adicionarFiltro();
//
//																			
//										
//										if(relatorioPre == null || relatorioPre.getId() == null){
//											
//											relatorioPre = new RelatorioPre(null, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI(),	codSubModulo, tfNomeRelatorio.getValue(),tipo,ordenacao,orientacao,resumo, new Date());
//										}else{
//											relatorioPre.setNome_relatorio(tfNomeRelatorio.getValue());
//											relatorioPre.setOrdenacao_relatorio(ordenacao);
//											relatorioPre.setOrientacao(orientacao);
//											relatorioPre.setResumo(resumo);
//											relatorioPre.setTipo_relatorio(tipo);
//											
//										}
//										
//										filtrosRelatorio = new ArrayList<>();
//										
//										for (SearchParameters search : listaParametros) {
//											filtrosRelatorio.add(new RelatorioPreFiltro(null, relatorioPre, search.getCampo(),search.getOperador(), search.getValor()));
//										}
//										
//										colunasVisiveis = new ArrayList<>();
//										Object[] visible_columns = tb.getVisibleColumns();		
//										for(Object c:visible_columns){
//											if(!tb.isColumnCollapsed(c.toString())){					   	
//												colunasVisiveis.add(new RelatorioPreColunas(null, relatorioPre, c.toString()));
//											}
//										}
//											
//										RelatorioDAO relatoriDAO = new RelatorioDAO();
//										
//										if(relatorioPre.getNome_relatorio()!=null && !relatorioPre.getNome_relatorio().equals("")){
//											relatoriDAO.addRelatorio(relatorioPre, filtrosRelatorio,colunasVisiveis);											
//										
//											Notification.show("Relatório Salvo com Sucesso", Type.ERROR_MESSAGE);
//											btSalvar.setEnabled(false);
//											btImprimir.setEnabled(true);
//											btGerarChart.setEnabled(true);
//										}
//									}catch (Exception e) {
//										e.printStackTrace();
//									}
//									
//									
//								}
//									
//								
//			 				}
//						});
//						
//						v.addListerner(new NovoRelatorioOse.AddFiltroOseListerner() {
//							
//							@Override
//							public void onClose(AddFiltroOseEvent event) {
//								try{
//									listaParametros = event.getFiltros();
//									adicionarFiltro();
//								
//								}catch (Exception e) {
//									e.printStackTrace();
//								}
//							}
//						});
//						
//						
//						getUI().addWindow(v);
//				
//				}else{
//					Notification.show("Você precisa Carregar um Relatório Salvo para Editar Filtros!", Type.ERROR_MESSAGE);
//				}
//			}
//		});
//		btEditar.setEnabled(false);
//		return btEditar;
//	}
	
//	public Button BuildbtNovo() {
//		btNovo = new Button("Novo Filtro", new Button.ClickListener() {
//			
//			
//			public void buttonClick(ClickEvent event) {
//								
//				relatorioPre = new RelatorioPre();
//				listaParametros = new ArrayList<>();
//				tipo = null;
//				ordenacao = null;
//				tfNomeRelatorio.setReadOnly(false);
//				tfNomeRelatorio.setValue("");
//				tfNomeRelatorio.setReadOnly(true);
//				
//				
//				container.removeAllContainerFilters();
//				container.addContainerFilter(Filters.eq("empresa_id", OpusERP4UI.getEmpresa().getId()));
//				container.applyFilters();
//				
//				hlFloat.replaceComponent(lbRegistros, lbRegistros());
//				
//				NovoRelatorioOse v = new NovoRelatorioOse("Novo Relatório Roterização", true);
//				v.addListerner(new NovoRelatorioOse.RelatorioOseListerner() {
//					
//					@Override
//					public void onClose(RelatorioOseEvent event) {
//						if(event.isConfirm()){
//							try{
//								listaParametros = event.getParametros();
//								tipo = event.getTipo();
//								ordenacao = event.getOrdenacao();
//								orientacao = event.getOrientacao();
//								resumo = event.getResumo();
//								
//								adicionarFiltro();
//								btSalvar.setEnabled(true);
//								btImprimir.setEnabled(true);
//								btEditar.setEnabled(true);
//							}catch(Exception e){
//								e.printStackTrace();
//							}
//						}else{
//							relatorioPre = new RelatorioPre();
//							listaParametros = new ArrayList<>();
//							tipo = null;
//							ordenacao = null;
//							
//							tfNomeRelatorio.setReadOnly(false);
//							tfNomeRelatorio.setValue("");
//							tfNomeRelatorio.setReadOnly(true);
//							btSalvar.setEnabled(false);
//							btEditar.setEnabled(false);
//							btGerarChart.setEnabled(false);
//							//btImprimir.setEnabled(false);
//						}
//	 				}
//				});
//				
//				v.addListerner(new NovoRelatorioOse.AddFiltroOseListerner() {
//					
//					@Override
//					public void onClose(AddFiltroOseEvent event) {
//						try{
//							listaParametros = event.getFiltros();
//							adicionarFiltro();
//						}catch(Exception e){
//							e.printStackTrace();
//						}
//					}
//				});
//				
//				
//				getUI().addWindow(v);
//			}
//		});
//		return btNovo;
//		
//	}
	

	private boolean newFile = true;
	
//	public Button BuildbtSalvar(){
//		btSalvar = new Button("Salvar", new Button.ClickListener() {
//			
//			
//			public void buttonClick(ClickEvent event) {
//								
//				final SalvarRelatorioOseEditor salvarEditor = new SalvarRelatorioOseEditor(codSubModulo,"Salvar Relatório", true);
//				salvarEditor.addListerner(new SalvarRelatorioOseEditor.SalvarRelatorioOseListerner() {
//					
//					@Override
//					public void onClose(SalvarRelatorioOseEvent event) {
//						if(!event.getNome().isEmpty() && listaParametros.size() >0 && !ordenacao.isEmpty() && !tipo.isEmpty()){
//							
//							tfNomeRelatorio.setReadOnly(false);
//							tfNomeRelatorio.setValue(event.getNome());
//							tfNomeRelatorio.setReadOnly(true);
//							
//							
//							if(relatorioPre == null || relatorioPre.getId() == null){
//								
//								relatorioPre = new RelatorioPre(null, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI(),	codSubModulo, event.getNome(),tipo,ordenacao,orientacao,resumo, new Date());
//							}else{
//								relatorioPre.setNome_relatorio(event.getNome());
//							}
//							
//							filtrosRelatorio = new ArrayList<>();
//							
//							for (SearchParameters search : listaParametros) {
//								filtrosRelatorio.add(new RelatorioPreFiltro(null, relatorioPre, search.getCampo(),search.getOperador(), search.getValor()));
//							}
//							
//							colunasVisiveis = new ArrayList<>();
//							Object[] visible_columns = tb.getVisibleColumns();		
//							for(Object c:visible_columns){
//								if(!tb.isColumnCollapsed(c.toString())){					   	
//									colunasVisiveis.add(new RelatorioPreColunas(null, relatorioPre, c.toString()));
//								}
//							}
//								
//							RelatorioDAO relatoriDAO = new RelatorioDAO();
//							relatoriDAO.addRelatorio(relatorioPre, filtrosRelatorio,colunasVisiveis);
//							
//							Notification.show("Relatório Salvo com Sucesso", Type.ERROR_MESSAGE);
//							btSalvar.setEnabled(false);
//						}else{					
//								
////							getUI().addWindow(salvarEditor);
//						//	Notification.show("Informe um Nome para o Relatório e Parametros de Busca", Type.ERROR_MESSAGE);
//						}
//					}
//				});
//							
//				getUI().addWindow(salvarEditor);
//						
//			}
//		});
//		btSalvar.setEnabled(false);
//		return btSalvar;
//	}
	
	
//	public Button BuildbtCarregar(){
//		btCarregar = new Button("Carregar", new Button.ClickListener() {
//			
//			public void buttonClick(ClickEvent event) {
//								
//				winRelatorios = new Window("Relatórios Salvos");
//				winRelatorios.setWidth("850px");
//				winRelatorios.setHeight("360px");
//				winRelatorios.center();
//				winRelatorios.setModal(true);
//				winRelatorios.setResizable(false);
//				winRelatorios.setClosable(false);
//				
//				winRelatorios.setContent(new VerticalLayout(){
//					{
//						setSizeFull();
//						setMargin(true);
//						
//						TextField tfBusca = new TextField();
//						tfBusca.setInputPrompt("Buscar...");
//						tfBusca.setWidth("100%");
//						tfBusca.setTextChangeEventMode(TextChangeEventMode.LAZY);
//						tfBusca.focus();
//						tfBusca.addListener(new FieldEvents.TextChangeListener() {
//							
//							@Override
//							public void textChange(TextChangeEvent event) {
//								addFilter(event.getText());
//							}
//						});
//						
//						addComponent(tfBusca);
//						
//
//						//Busca Relatorios
//						relatoriosContainer = JPAContainerFactory.make(RelatorioPre.class, ConnUtil.getEntity());
//						relatoriosContainer.addContainerFilter(Filters.eq("codEmpresa", OpusERP4UI.getEmpresa().getId()));
//						relatoriosContainer.addContainerFilter(Filters.eq("codUsuario", OpusERP4UI.getUsuarioLogadoUI()));
//						relatoriosContainer.addContainerFilter(Filters.eq("codSubModulo", getCodSubModulo()));
//						relatoriosContainer.addNestedContainerProperty("codUsuario.username");
//						
//						relatoriosContainer.sort(new Object[]{"data_cadastro"}, new boolean[]{false});
//
//						tbRelatorios = new Table(null, relatoriosContainer);
//						
//						final Button btOk = new Button("Carregar", new Button.ClickListener() {
//							
//							public void buttonClick(ClickEvent event) {
//								RelatorioDAO relatorioDAO = new RelatorioDAO();								
//								relatorioPre = relatorioDAO.find((Integer)tbRelatorios.getItem(tbRelatorios.getValue()).getItemProperty("id").getValue());
//								
//								if(relatorioPre != null && relatorioPre.getId() != null){
//									
//									
//									tfNomeRelatorio.setReadOnly(false);
//									tfNomeRelatorio.setValue(relatorioPre.getNome_relatorio());
//									tfNomeRelatorio.setReadOnly(true);
//									
//									tipo = relatorioPre.getTipo_relatorio();
//									ordenacao = relatorioPre.getOrdenacao_relatorio();
//									orientacao = relatorioPre.getOrientacao();
//									resumo = relatorioPre.getResumo();
//									//Chamar Função de Montar
//									List<RelatorioPreFiltro> filtros = relatorioDAO.getFiltros(relatorioPre);
//									List<RelatorioPreColunas> colunas = relatorioDAO.getColunas(relatorioPre);											
//									
//									
//									if(filtros != null){
//										loadRelatorio(filtros);
//									}
//									
//									if(colunas != null){
//										loadRelatorioColuna(colunas);
//									}
//									
//									winRelatorios.close();
//									//change = false;								
//
//									btSalvar.setEnabled(false);
//									btImprimir.setEnabled(true);
//									btEditar.setEnabled(true);	
//									btGerarChart.setEnabled(true);
//									//change = false;
//								
//								}
//							}				
//							
//						});
//						
//						btOk.setEnabled(false);
//						btOk.setStyleName("default");
//						
//						
//						ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.ENTER,null) {
//							
//							@Override
//							public void handleAction(Object sender, Object target) {
//								btOk.click();
//							}
//						};
//						
//						btOk.addShortcutListener(slbtOK);
//						
//						
//						tbRelatorios.setWidth("100%");
//						tbRelatorios.setHeight("230px");
//						tbRelatorios.setSelectable(true);          
//						tbRelatorios.setVisibleColumns(new Object[]{"nome_relatorio","data_cadastro","codUsuario.username","tipo_relatorio","ordenacao_relatorio",
//								"orientacao","resumo"});
//
//						tbRelatorios.setColumnHeader("nome_relatorio", "Nome");
//						tbRelatorios.setColumnHeader("data_cadastro", "Data de Criação");
//						tbRelatorios.setColumnHeader("codUsuario.username", "Autor");
//						tbRelatorios.setColumnHeader("tipo_relatorio", "Tipo");
//						tbRelatorios.setColumnHeader("ordenacao_relatorio", "Ordenação");
//						tbRelatorios.setColumnHeader("orientacao", "Orientação");
//						tbRelatorios.setColumnHeader("resumo", "Resumo");
//						
//						tbRelatorios.setColumnExpandRatio("nome_relatorio", 1);
//						
//						
//						
//						tbRelatorios.setImmediate(true);
//						
//						tbRelatorios.addListener(new Property.ValueChangeListener() {
//							
//							@Override
//							public void valueChange(ValueChangeEvent event) {
//								if(tbRelatorios.getValue() != null){
//									btOk.setEnabled(true);
//								}else{
//									btOk.setEnabled(false);
//								}
//							}
//						});
//						
//						
//						tbRelatorios.addGeneratedColumn("x", new Table.ColumnGenerator() {
//							
//							@Override
//							public Object generateCell(final Table source, final Object itemId, Object columnId) {
//								
//								
//								
//								Button btDeletar = new Button(null, new Button.ClickListener() {
//									
//									@Override
//									public void buttonClick(ClickEvent event) {
//										GenericDialog gd = new GenericDialog("Confirme para Continuar", "Você deseja realmente Excluir este Relatório?", true,true);
//										gd.addListerner(new GenericDialog.DialogListerner() {
//											
//											@Override
//											public void onClose(DialogEvent event) {
//												if(event.isConfirm()){		
//													Integer codRelatorio  = (Integer)tbRelatorios.getItem(itemId).getItemProperty("id").getValue();
//													RelatorioDAO relatorioDAO = new RelatorioDAO();
//													boolean check = relatorioDAO.apagarRelatorio(codRelatorio);
//													
//													if(check){
//														Notification.show("Relatório foi Excluído com Sucesso!");
//														
//														if(relatoriosContainer != null){
//															refresh();
//															relatoriosContainer.refresh();
//														}
//													}else{
//														Notification.show("Não foi Possivel Realizar a Exclusão do Relatório");
//													}										
//												}
//											}
//										});
//										
//										getUI().addWindow(gd);
//									}
//								});
//								btDeletar.setIcon(new ThemeResource("icons/btDeletar.png"));
//								btDeletar.setStyleName(BaseTheme.BUTTON_LINK);
//								btDeletar.setDescription("Deletar Relatório");
//								
//								return btDeletar;
//							}
//						});
//
//						tbRelatorios.setColumnWidth("x", 20);
//								
//						addComponent(tbRelatorios);
//						setExpandRatio(tbRelatorios, 2);
//						
//						HorizontalLayout hlButtons = new HorizontalLayout();
//						hlButtons.setSpacing(true);
//						hlButtons.setMargin(true);
//						hlButtons.setStyleName("hl_buttons_bottom");
//						hlButtons.addComponent(new Button("Cancelar", new Button.ClickListener() {
//							
//							@Override
//							public void buttonClick(ClickEvent event) {
//									winRelatorios.close();				
//							}
//						}));
//						
//						hlButtons.addComponent(btOk);
//						
//						addComponent(hlButtons);
//						setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
//						
//						
//						ShortcutListener slbtCancelar = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
//							
//							@Override
//							public void handleAction(Object sender, Object target) {
//								winRelatorios.close();
//							}
//						};
//						hlButtons.addShortcutListener(slbtCancelar);
//					}
//				});
//				
//				getUI().addWindow(winRelatorios);				
//			}		
//		});
//		
//		return btCarregar;
//	}
	
	public Button BuildbtGerarChart(){
		btGerarChart = new Button("Gerar Gráfico", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), OpusERP4UI.getUsuarioLogadoUI().getId(), "Gerar Grafico"))				
				{
			
					try {
						
						List<Object> columns = new ArrayList<Object>();
						Object[] visible_columns = tb.getVisibleColumns();		
						for(Object c:visible_columns){
							if(!tb.isColumnCollapsed(c.toString())){					   	
								columns.add(c);
							}	
						}
						
						
						// INSTANCIA UMA NOVA JANELA E ADICIONA SUAS PROPRIEDADES
						Window win = new Window("Gráfico de Resumo");
						win.setWidth("800px");
						win.setHeight("600px");
						win.setResizable(false);
						win.center();
						win.setModal(true);
						win.setStyleName("disable_scroolbar");
	
				        final Chart chart = new Chart(ChartType.COLUMN);
				        
				        setCaption(relatorioPre.getNome_relatorio());
				        chart.getConfiguration().setTitle(relatorioPre.getNome_relatorio());
				        
				        chart.getConfiguration().getxAxis().getLabels().setEnabled(false);
				        chart.getConfiguration().getxAxis().setTickWidth(0);
				        chart.getConfiguration().setExporting(true);
				        chart.setWidth("100%");
				        chart.setHeight("100%");		        
				        
						TypedQuery qGroup = gerarDadosGRaficos();		        
				        final List<Series> seriesColum = new ArrayList<Series>();
				        final DataSeries seriesPie = new DataSeries();
				        
				        for(Ose c:(List<Ose>) qGroup.getResultList())
				        {       	
				        	seriesColum.add(new ListSeries(c.getColuna(),c.getQtd()));
				        	seriesPie.add(new DataSeriesItem(c.getColuna(), c.getQtd()));
				        	
				        }        
				       
				        chart.getConfiguration().setSeries(seriesColum);
									        
				        final VerticalLayout vlChart = new VerticalLayout();
				        vlChart.setWidth("100%");
				        vlChart.setHeight("100%");
				        			        
				        final ComboBox cbTipoGrafico = new ComboBox();
				        cbTipoGrafico.setNullSelectionAllowed(false);
				        cbTipoGrafico.addStyleName("margin-right");
				        cbTipoGrafico.addItem("COLUNA");			        
				        cbTipoGrafico.addItem("BARRA");
				        cbTipoGrafico.addItem("PIZZA");
				        
				        
				        cbTipoGrafico.select("COLUNA");
				        cbTipoGrafico.setImmediate(true);
				        cbTipoGrafico.addValueChangeListener(new Property.ValueChangeListener() {
							
							@Override
							public void valueChange(ValueChangeEvent event) {
								
								if(cbTipoGrafico.getValue().toString().equals("COLUNA")){
									 chart.getConfiguration().getChart().setType(ChartType.COLUMN);
									 chart.getConfiguration().setSeries(seriesColum);
									
									 chart.drawChart();
								}else if(cbTipoGrafico.getValue().toString().equals("BARRA")){
									 chart.getConfiguration().getChart().setType(ChartType.BAR);
									 chart.getConfiguration().setSeries(seriesColum);
									
									 chart.drawChart();
								}else if(cbTipoGrafico.getValue().toString().equals("PIZZA")){
									 chart.getConfiguration().getChart().setType(ChartType.PIE);
									 chart.getConfiguration().setSeries(seriesPie);
									
									 PlotOptionsPie plotOptions = new PlotOptionsPie();
								     plotOptions.setAllowPointSelect(true);
								     plotOptions.setCursor(Cursor.POINTER);
								     plotOptions.setShowInLegend(true);
								     Labels dataLabels = new Labels();
								     dataLabels.setEnabled(true);
								     chart.getConfiguration().setPlotOptions(plotOptions);
									
									 chart.drawChart();
								}
								
								vlChart.replaceComponent(chart, chart);
								vlChart.setExpandRatio(chart, 1);
								
							}
						});
				        
				        HorizontalLayout hlTopo = new HorizontalLayout();
				        hlTopo.setWidth("100%");
				        hlTopo.addComponent(cbTipoGrafico);
				        hlTopo.setComponentAlignment(cbTipoGrafico, Alignment.TOP_RIGHT);
				        
				        vlChart.addComponent(hlTopo);
				        vlChart.addComponent(chart);
				        vlChart.setExpandRatio(chart, 1);
				        
						win.setContent(vlChart);
						getUI().addWindow(win);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						System.out.println("Erro: "+e.getMessage());
						System.out.println("Causado por: "+e.getCause());
					}	
				}else{
					Notification.show("Atenção", "Você não Possui Permissão para Gerar Gráfico", Type.ERROR_MESSAGE);	
				}
			}
		});
		
		btGerarChart.setEnabled(false);
		
		return btGerarChart;
	}
	
	private TypedQuery<Ose> gerarDadosGRaficos(){
		
		EntityManager em = ConnUtil.getEntity();
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Ose> criteriaQuery = cb.createQuery(Ose.class);
		Root<Ose> rootContas = criteriaQuery.from(Ose.class);
		EntityType<Ose> type = em.getMetamodel().entity(Ose.class);

		List<Predicate> criteria = new ArrayList<Predicate>();

		criteria.add(cb.equal(rootContas.get("empresa_id"), OpusERP4UI.getEmpresa().getId()));

		if (listaParametros.size() > 0) {
			
			try{
				for (SearchParameters s : listaParametros) {
	

					if (s.getOperador().equals("IGUAL")) {
						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("nome_razao")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("subgrupo.nome")) {
							criteria.add(cb.like(cb.lower(rootContas.get("subgrupo").<String>get("nome")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("grupo.nome")) {
							criteria.add(cb.like(cb.lower(rootContas.get("grupo").<String>get("nome")), s.getValor().toLowerCase()));
						}	
						
						if (s.getCampo().equals("tipo_subgrupo.nome")) {
							criteria.add(cb.like(cb.lower(rootContas.get("tipo_subgrupo").<String>get("nome")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("veiculo_id.cod_veiculo")) {
							criteria.add(cb.like(cb.lower(rootContas.get("veiculo_id").<String>get("cod_veiculo")), s.getValor().toLowerCase()));
						}					

						if (!s.getCampo().equals("subgrupo.nome") && !s.getCampo().equals("cliente.nome_razao") && 
							!s.getCampo().equals("grupo.nome")&& !s.getCampo().equals("tipo_subgrupo.nome")&&!s.getCampo().equals("veiculo_id.cod_veiculo")) {
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
								criteria.add(cb.equal(rootContas.<Date>get(s.getCampo()), sdf.parse(s.getValor())));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(String.class)){
								criteria.add(cb.like(cb.lower(rootContas.<String>get(s.getCampo())), s.getValor().toLowerCase()));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.equal(rootContas.<Integer>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
						}

					}else if (s.getOperador().equals("DIFERENTE")) {
						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("nome_razao")), s.getValor().toLowerCase()));
						}	
						if (s.getCampo().equals("subgrupo.nome")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("subgrupo").<String>get("nome")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("grupo.nome")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("grupo").<String>get("nome")), s.getValor().toLowerCase()));
						}	
						if (s.getCampo().equals("tipo_subgrupo.nome")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("tipo_subgrupo").<String>get("nome")), s.getValor().toLowerCase()));
						}					
						if (s.getCampo().equals("veiculo_id.cod_veiculo")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("veiculo_id").<String>get("cod_veiculo")), s.getValor().toLowerCase()));
						}					

						if (!s.getCampo().equals("subgrupo.nome") && !s.getCampo().equals("cliente.nome_razao") && 
							!s.getCampo().equals("grupo.nome")&& !s.getCampo().equals("tipo_subgrupo.nome")&&!s.getCampo().equals("veiculo_id.cod_veiculo")) {		
														
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
								criteria.add(cb.notEqual(rootContas.<Date>get(s.getCampo()), sdf.parse(s.getValor())));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(String.class)){
								criteria.add(cb.notLike(cb.lower(rootContas.<String> get(s.getCampo())), s.getValor().toLowerCase()));
							}
							
							if(rootContas.get(s.getCampo()).getJavaType().equals(Integer.class)){
								criteria.add(cb.notEqual(rootContas.<Integer>get(s.getCampo()), s.getValor().toLowerCase()));
							}
							
							
						}

					} else if (s.getOperador().equals("CONTEM")) {
						
						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.like(cb.lower(rootContas.get("cliente").<String>get("nome_razao")), "%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("subgrupo.nome")) {
							criteria.add(cb.like(cb.lower(rootContas.get("subgrupo").<String>get("nome")), "%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("grupo.nome")) {
							criteria.add(cb.like(cb.lower(rootContas.get("grupo").<String>get("nome")), "%" + s.getValor().toLowerCase()+ "%"));
						}	
						
						if (s.getCampo().equals("tipo_subgrupo.nome")) {
							criteria.add(cb.like(cb.lower(rootContas.get("tipo_subgrupo").<String>get("nome")), "%" + s.getValor().toLowerCase()+ "%"));
						}																					
						if (s.getCampo().equals("veiculo_id.cod_veiculo")) {
							criteria.add(cb.like(cb.lower(rootContas.get("veiculo_id").<String>get("cod_veiculo")), "%" + s.getValor().toLowerCase()+ "%"	));
						}					
						
					} else if (s.getOperador().equals("NAO CONTEM")) {
						

						if (s.getCampo().equals("cliente.nome_razao")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("cliente").<String>get("nome_razao")),"%" + s.getValor().toLowerCase()+ "%"));
						}
						if (s.getCampo().equals("subgrupo.nome")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("subgrupo").<String>get("nome")), "%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("grupo.nome")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("grupo").<String>get("nome")), "%" + s.getValor().toLowerCase()+ "%"));
						}	
						
						if (s.getCampo().equals("tipo_subgrupo.nome")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("tipo_subgrupo").<String>get("nome")), "%" + s.getValor().toLowerCase()+ "%"));
						}					
						if (s.getCampo().equals("veiculo_id.cod_veiculo")) {
							criteria.add(cb.notLike(cb.lower(rootContas.get("veiculo_id").<String>get("cod_veiculo")), "%" + s.getValor().toLowerCase()+ "%"));
						}					

						if (!s.getCampo().equals("subgrupo.nome") && !s.getCampo().equals("cliente.nome_razao") &&
								!s.getCampo().equals("grupo.nome")&& !s.getCampo().equals("tipo_subgrupo.nome") && !s.getCampo().equals("veiculo_id.cod_veiculo")) {
																
							criteria.add(cb.notLike(cb.lower(rootContas.<String> get(s.getCampo())),"%" + s.getValor().toLowerCase() + "%"));
						}
					} else if (s.getOperador().equals("MAIOR QUE")) {

							try{						
								if(rootContas.get(s.getCampo()).getJavaType().equals(Integer.class)){
									criteria.add(cb.greaterThan(rootContas.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
								}
								
								if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){								
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
									criteria.add(cb.greaterThan(rootContas.<Date> get(s.getCampo()), sdf.parse(s.getValor())));
								}
								
							}catch(Exception e)
							{
								e.printStackTrace();		
						}
					} else if (s.getOperador().equals("MENOR QUE")) {

							try{						
								if(rootContas.get(s.getCampo()).getJavaType().equals(Integer.class)){
									criteria.add(cb.lessThan(rootContas.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
								}
								
								if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
									criteria.add(cb.lessThan(rootContas.<Date> get(s.getCampo()),  sdf.parse(s.getValor())));
								}
							}catch(Exception e)
							{
								e.printStackTrace();
							}
							
						
					} else if (s.getOperador().equals("MAIOR IGUAL QUE")) {

							try{						
								if(rootContas.get(s.getCampo()).getJavaType().equals(Integer.class)){
									criteria.add(cb.greaterThanOrEqualTo(rootContas.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
								}
								
								if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
									criteria.add(cb.greaterThanOrEqualTo(rootContas.<Date> get(s.getCampo()), sdf.parse(s.getValor())));
								}
							}catch(Exception e)
							{
								e.printStackTrace();
							}
		
					} else if (s.getOperador().equals("MENOR IGUAL QUE")) {

							try{						
								if(rootContas.get(s.getCampo()).getJavaType().equals(Integer.class)){
									criteria.add(cb.lessThanOrEqualTo(rootContas.<Integer> get(s.getCampo()), Integer.valueOf(s.getValor())));
								}
								
								if(rootContas.get(s.getCampo()).getJavaType().equals(Date.class)){
									SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
									criteria.add(cb.lessThanOrEqualTo(rootContas.<Date> get(s.getCampo()), sdf.parse(s.getValor())));
								}
							}catch(Exception e)
							{
								e.printStackTrace();
							}
						}
				}
			
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		if (criteria.size() == 0) {
			throw new RuntimeException("no criteria");
		} else if (criteria.size() == 1) {
			criteriaQuery.where(criteria.get(0));
		} else {
			criteriaQuery.where(cb.and(criteria.toArray(new Predicate[0])));
		}

		TypedQuery q = em.createQuery(criteriaQuery);
        
        
		CriteriaQuery<Ose> criteriaQueryGroup = cb.createQuery(Ose.class);
		Root<Ose> rootGroup = criteriaQueryGroup.from(Ose.class);	
		
		if (selectFiltro(resumo).equals("cliente.nome_razao")) {
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("cliente").get("nome_razao");		
			criteriaQueryGroup.groupBy(rootGroup.join("cliente").get("nome_razao"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(Ose.class,coluna, qtd));
		}
		
		if (selectFiltro(resumo).equals("subgrupo.nome")) {				
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("subgrupo").get("nome");				
			criteriaQueryGroup.groupBy(rootGroup.join("subgrupo").get("nome"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(Ose.class,coluna, qtd));
		}

		if (selectFiltro(resumo).equals("grupo.nome")) {				
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("grupo").get("nome");				
			criteriaQueryGroup.groupBy(rootGroup.join("grupo").get("nome"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(Ose.class,coluna, qtd));
		}
	
		if (selectFiltro(resumo).equals("tipo_subgrupo.nome")) {				
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("tipo_subgrupo").get("nome");				
			criteriaQueryGroup.groupBy(rootGroup.join("tipo_subgrupo").get("nome"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(Ose.class,coluna, qtd));
		}
		if (selectFiltro(resumo).equals("veiculo_id.cod_veiculo")) {				
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get("veiculo_id").get("cod_veiculo");				
			criteriaQueryGroup.groupBy(rootGroup.join("veiculo_id").get("cod_veiculo"));			
			criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));			
			criteriaQueryGroup.select(cb.construct(Ose.class,coluna, qtd));
		}
		
		if (!selectFiltro(resumo).equals("tipo_subgrupo.nome") && !selectFiltro(resumo).equals("cliente.nome_razao") &&
			!selectFiltro(resumo).equals("grupo.nome") && !selectFiltro(resumo).equals("subgrupo.nome") && !selectFiltro(resumo).equals("veiculo_id.cod_veiculo")) {
			
			
			Selection<Long> qtd = cb.count(rootGroup).alias("qtd");			
			Selection<String> coluna = rootGroup.get(selectFiltro(resumo));				
			criteriaQueryGroup.groupBy(rootGroup.get(selectFiltro(resumo)));			
			
			if (criteria.size() == 0) {
				throw new RuntimeException("no criteria");
			} else if (criteria.size() == 1) {					
				criteriaQueryGroup.where(criteria.get(0));
			} else {					
				criteriaQueryGroup.where(cb.and(criteria.toArray(new Predicate[0])));
			}
			
			
			criteriaQueryGroup.select(cb.construct(Ose.class,coluna, qtd));
			
			
		}
		return em.createQuery(criteriaQueryGroup);	
	}
	
	Window winSubMenuNovo;
	private void buildSubMenu(ClickEvent event) {
		winSubMenuNovo = new Window("Escolha uma das Opções:");
        VerticalLayout l = new VerticalLayout();
        //l.setMargin(true);
        //l.setSpacing(true);
        winSubMenuNovo.setContent(l);
        winSubMenuNovo.setWidth("300px");
        winSubMenuNovo.addStyleName("notifications");
        winSubMenuNovo.setClosable(false);
        winSubMenuNovo.setResizable(false);
        winSubMenuNovo.setDraggable(false);
        winSubMenuNovo.setPositionX(event.getClientX() - event.getRelativeX());
        winSubMenuNovo.setPositionY(event.getClientY() - event.getRelativeY());
        winSubMenuNovo.setCloseShortcut(KeyCode.ESCAPE, null);
        
              
        Button bt1 = new Button("Gerar PDF", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(gmDAO.checkPermissaoEmpresaSubModuloUsuario(codSubModulo, OpusERP4UI.getEmpresa().getId(), 
						OpusERP4UI.getUsuarioLogadoUI().getId(), "Gerar PDF"))				
				{				
				
					try {
						
						List<Object> columns = new ArrayList<Object>();
						Object[] visible_columns = tb.getVisibleColumns();		
						for(Object c:visible_columns){
							if(!tb.isColumnCollapsed(c.toString())){					   	
								columns.add(c);
							}	
						}
	
						if(tfNomeRelatorio.getValue()!=null && !tfNomeRelatorio.getValue().equals("")){
							nomeRelatorio = " - "+relatorioPre.getNome_relatorio()+".pdf";
						}else{
							nomeRelatorio = ".pdf";
						}
						// INSTANCIA UMA NOVA JANELA E ADICIONA SUAS PROPRIEDADES
						Window win = new Window("Relatório de Roterização");
						win.setWidth("800px");
						win.setHeight("600px");
						win.setResizable(true);
						win.center();
						win.setModal(true);
						win.setStyleName("disable_scroolbar");
						
						StreamResource resource = new StreamResource(new ExportarRelatorioOse(tipo,ordenacao,orientacao,resumo,listaParametros,columns), "RELATORIO DE ROTEIRIZACAO"+nomeRelatorio);
						resource.getStream();
						resource.setMIMEType("application/pdf");
						resource.setCacheTime(0);
						
						Embedded e = new Embedded();
						e.setSizeFull();
						e.setType(Embedded.TYPE_BROWSER);
						e.setSource(resource);
						
						win.setContent(e);
						getUI().addWindow(win);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						System.out.println("Erro: "+e.getMessage());
						System.out.println("Causado por: "+e.getCause());
					}
				
				}else{					
					Notify.Show("Você não Possui Permissão para Gerar PDF", Notify.TYPE_ERROR);
				}
			}
		});
        bt1.setPrimaryStyleName("btSubMenu");
        
        ExcelExporter bt2 = new ExcelExporter(tb);
        bt2.setCaption("Gerar XLS");
        bt2.setDownloadFileName(relatorioPre.getNome_relatorio());
        bt2.setPrimaryStyleName("btSubMenu");
        bt2.setEnabled(true);
        
        l.addComponent(bt1);        
        l.addComponent(bt2);
       
    }

	public Button BuildbtImprimir(){
		btImprimir = new Button("Gerar", new Button.ClickListener() {
			
			
			public void buttonClick(ClickEvent event) {
				
				 if (winSubMenuNovo != null && winSubMenuNovo.getUI() != null)
					 winSubMenuNovo.close();
	             else {
	            	 
	            	 	            	             	 
				     buildSubMenu(event);
				     
					 getUI().addWindow(winSubMenuNovo);
					 winSubMenuNovo.focus();
	                 ((CssLayout) getUI().getContent()).addLayoutClickListener(new LayoutClickListener() {
	                             
	               			@Override
	                        public void layoutClick(LayoutClickEvent event) {
	              				winSubMenuNovo.close();
	                            ((CssLayout) getUI().getContent()).removeLayoutClickListener(this);
	                        }
	                 });
	             }
				
				
		}
	});
		
		
		btImprimir.setEnabled(false);
		
		return btImprimir;
		
	}
	
	

	public Integer getCodSubModulo() {
		return codSubModulo;
	}

	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}

	
	
}
