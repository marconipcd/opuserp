package com.digital.opuserp.view.modulos.acesso.contratos_acesso;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.digital.opuserp.dao.ProdutoDAO;
import com.digital.opuserp.domain.Cliente;
import com.digital.opuserp.domain.ContratosAcesso;
import com.digital.opuserp.domain.Produto;
import com.digital.opuserp.domain.SerialProduto;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.view.util.MaterialUtil;
import com.digital.opuserp.view.util.MaterialUtil.MaterialEvent;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.addon.jpacontainer.filter.Filters;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class EditarRegimeComodatoParaComodatoTotalEditor extends Window {

	Item item;
	Button btSalvar; 
	Button btCancelar;
	FormLayout flPrincipal;
	FieldGroup fieldGroup;
	VerticalLayout vlRoot;
	String motivo;
	ComboBox cbProduto;
	
	Produto roteador_selecionado;
	Produto onu_selecionado;
	private TextField tfDescricaoRoteador;
	private TextField tfDescricaoOnu;
	private ComboBox cbSerialRoteador;
	private ComboBox cbSerialOnu;
	
	private JPAContainer<SerialProduto> containerSeriais;
		
	boolean valid_mac = false;
	boolean valid_serial = false;
	
	//private TextField txtSerial;
	private ContratosAcesso contratoAcesso;

	public EditarRegimeComodatoParaComodatoTotalEditor(Item item, String title, boolean modal){
		this.item = item;
		
		contratoAcesso = (ContratosAcesso) item.getItemProperty("contrato").getValue();
		
		setWidth("751px");
				
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
	

	private void buildLayout() {
		
		fieldGroup = new FieldGroup(item);
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					TextField tfCodContrato = new TextField("Contrato");
					tfCodContrato.setValue(item.getItemProperty("id").toString());
					tfCodContrato.setReadOnly(true);
					tfCodContrato.setWidth("62px");
					tfCodContrato.setStyleName("caption-align");
					
					addComponent(tfCodContrato);
				}
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					Cliente cliente = (Cliente) item.getItemProperty("cliente").getValue();
					
					TextField tfNomeCliente = new TextField("Titular Atual");
					tfNomeCliente.setValue(cliente.getNome_razao());
					tfNomeCliente.setReadOnly(true);
					tfNomeCliente.setWidth("360px");
					tfNomeCliente.setStyleName("caption-align");
					
					addComponent(tfNomeCliente);
				}
		});	
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
					
					
										
					TextField txtRegime = new TextField("Regime Atual");
										
					txtRegime.setValue(item.getItemProperty("regime").getValue().toString());
					txtRegime.setStyleName("caption-align");					
					txtRegime.setRequired(true);
					txtRegime.setWidth("241px");
					txtRegime.setReadOnly(true);
					
					addComponent(txtRegime);
					
				}
		});	
		
		
		
		final Produto materialAntigo = item.getItemProperty("material").getValue() != null ? (Produto) item.getItemProperty("material").getValue() : null;
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
					
					
					TextField tfMaterialAtual = new TextField("Roteador Atual");
					tfMaterialAtual.setValue(materialAntigo != null ? materialAntigo.getNome() : "NENHUM");
					tfMaterialAtual.setReadOnly(true);
					tfMaterialAtual.setWidth("360px");
					tfMaterialAtual.setStyleName("caption-align");
					
					addComponent(tfMaterialAtual);
				}
		});	
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					String enderecoMac = item.getItemProperty("endereco_mac").getValue()  != null ? item.getItemProperty("endereco_mac").getValue().toString() : "";
					
					TextField tfEnderecoMac = new TextField("Endere??o MAC atual");
					tfEnderecoMac.setValue(enderecoMac);
					tfEnderecoMac.setReadOnly(true);
					tfEnderecoMac.setWidth("147px");
					tfEnderecoMac.setStyleName("caption-align");
					
					addComponent(tfEnderecoMac);
				}
		});	
		
		
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom-new");
					setMargin(true);
					setSpacing(true);
					
					TextField txtNovoRegime = new TextField("Novo Regime");
					txtNovoRegime.setStyleName("caption-align");					
					txtNovoRegime.setRequired(true);
					txtNovoRegime.setWidth("241px");
					txtNovoRegime.setValue("COMODATO (TOTAL)");
					txtNovoRegime.setReadOnly(true);
					
					addComponent(txtNovoRegime);
					//fieldGroup.bind(txtNovoRegime, "regime");
				}
		});	
			
		vlRoot.addComponent(new HorizontalLayout(){
			{						
				final TextField txtCodOnu = new TextField("Onu Novo");				
				txtCodOnu.setWidth("60px");				
				txtCodOnu.setNullRepresentation("");
				txtCodOnu.setStyleName("caption-align");
				txtCodOnu.setImmediate(true);
				txtCodOnu.setId("txtCodOnu");
				
				JavaScript.getCurrent().execute("$('#txtCodOnu').mask('00000000')");						
								
				txtCodOnu.addTextChangeListener(new FieldEvents.TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						ProdutoDAO cDAO = new ProdutoDAO();
						onu_selecionado = new Produto();
																	
						if(event.getText()!=null && 	!event.getText().isEmpty() && !event.getText().equals("")){														
							onu_selecionado = cDAO.find(Integer.parseInt(event.getText()));		
							
							if(onu_selecionado != null){
								tfDescricaoOnu.setReadOnly(false);
								tfDescricaoOnu.setValue(onu_selecionado.getNome());
								tfDescricaoOnu.setReadOnly(true);
								
								if(cbSerialOnu != null){
									cbSerialOnu.setEnabled(true);
									cbSerialOnu.setContainerDataSource(buildContainerSeriais(onu_selecionado.getId()));		
									cbSerialOnu.setItemCaptionPropertyId("serial");cbSerialOnu.setItemCaptionPropertyId("serial");
								}
							}else {
								tfDescricaoOnu.setReadOnly(false);
								tfDescricaoOnu.setValue("");
								tfDescricaoOnu.setReadOnly(true);		
								
								if(cbSerialOnu != null){
									cbSerialOnu.setEnabled(false);
									cbSerialOnu.setContainerDataSource(null);
								}						
							}
						}
					}
				});

				txtCodOnu.setRequired(true);		
				tfDescricaoOnu = new TextField();
				tfDescricaoOnu.setReadOnly(true);
				tfDescricaoOnu.setWidth("450px");			
								
				final Button btSearchOnu = new Button();
				btSearchOnu.setStyleName(BaseTheme.BUTTON_LINK);
				btSearchOnu.setIcon(new ThemeResource("icons/search-16.png"));
				btSearchOnu.addListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						
						MaterialUtil cUtil = new MaterialUtil(true, true);
						cUtil.addListerner(new MaterialUtil.MaterialListerner() {
							
							@Override
							public void onSelected(MaterialEvent event) {
									
									if(event.getMaterial() != null ){
										
										txtCodOnu.setValue(event.getMaterial().getId().toString());
										tfDescricaoOnu.setReadOnly(false);
										tfDescricaoOnu.setValue(event.getMaterial().getNome());
										tfDescricaoOnu.setReadOnly(true);
										onu_selecionado = event.getMaterial();		
										
										if(cbSerialOnu != null){
											cbSerialOnu.setEnabled(true);
											cbSerialOnu.setContainerDataSource(buildContainerSeriais(event.getMaterial().getId()));
											cbSerialOnu.setItemCaptionPropertyId("serial");
										}
									}
								}							
						});
						
						getUI().addWindow(cUtil);
					}
				});			
				
				FormLayout frmCodigoOnu = new FormLayout(){
					{						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new");		
												
						addComponent(txtCodOnu);							
					}
				};
				addComponent(frmCodigoOnu);
		
				FormLayout frmButtonSearchOnu =new FormLayout(){
					{						
						setMargin(true);
						setSpacing(true);
						setStyleName("form-cutom-new_hide_error_cell");							
						addComponent(btSearchOnu);							
					}
				}; 
							
				FormLayout frmDescOnu = new FormLayout(){
					{						
						setMargin(true);
						setSpacing(true);						
						setStyleName("form-cutom");		
						addStyleName("form-cutom-new_hide_require");
						
						addComponent(tfDescricaoOnu);							
					}
				}; 
				addComponent(frmButtonSearchOnu);
				addComponent(frmDescOnu);
				setExpandRatio(frmDescOnu, 1);	

			}	
		});
		
		vlRoot.addComponent(
				new FormLayout(){					
				{
					setStyleName("form-cutom");
					setMargin(true);
					setSpacing(true);
					
					cbSerialOnu = new ComboBox("Serial");
					cbSerialOnu.setEnabled(false);
					cbSerialOnu.setNullSelectionAllowed(false);
					cbSerialOnu.setStyleName("caption-align");
					cbSerialOnu.setRequired(true);					
					cbSerialOnu.setImmediate(true);
					
					cbSerialOnu.addValueChangeListener(new Property.ValueChangeListener() {
						
						@Override
						public void valueChange(ValueChangeEvent event) {
							if(cbSerialOnu.isValid()){
								valid_serial = true;
							}else{
								valid_serial = false;
							}
						}
					});
					
					addComponent(cbSerialOnu);
						
					}			
			});
		
	}

	private JPAContainer<SerialProduto> buildContainerSeriais(Integer codProduto){
		containerSeriais = JPAContainerFactory.make(SerialProduto.class, ConnUtil.getEntity());
		containerSeriais.addContainerFilter(Filters.eq("produto", new Produto(codProduto)));
		containerSeriais.addContainerFilter(Filters.eq("status", "ATIVO"));
		
		return containerSeriais;
	}
	
	
	public Button buildBtSalvar() {
		
	btSalvar = new Button("OK", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(valid_serial){
					try {	
						item.getItemProperty("regime").setValue("COMODATO (TOTAL)");
											
						item.getItemProperty("onu").setValue(onu_selecionado);
						item.getItemProperty("onu_serial").setValue(cbSerialOnu.getItem(cbSerialOnu.getValue()).getItemProperty("serial").getValue().toString());
						Integer codSerialOnu =Integer.parseInt(cbSerialOnu.getItem(cbSerialOnu.getValue()).getItemProperty("id").getValue().toString());
						
						fieldGroup.commit();				
						fireEvent(new EditarRegimeComodatoParaComodatoTotalEditorEvent(getUI(), item, motivo,codSerialOnu,  true));							
						
					} catch (Exception e) {											
						e.printStackTrace();
						Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);		
					}
				}else{
					Notify.Show_Invalid_Submit_Form();
				}
				
			}
		});
		

		ShortcutListener slbtOK = new ShortcutListener("Ok", ShortcutAction.KeyCode.ENTER,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btSalvar.click();
			}
		};
		
		btSalvar.addShortcutListener(slbtOK);		
		btSalvar.setStyleName("default");
		//btSalvar.setEnabled(false);
		return btSalvar;
	}


	
	public Button buildBtCancelar() {
			btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				if(!fieldGroup.isModified()){
					fieldGroup.discard();				
					fireEvent(new EditarRegimeComodatoParaComodatoTotalEditorEvent(getUI(), item,motivo,null,false));					
				}else{
					GenericDialog gDialog = new GenericDialog("Confirme para Continuar!", "Deseja Salvar as Informa????es Alteradas?", true, true);
					gDialog.setCaptionCANCEL("Sair sem Salvar!");
					gDialog.setCaptionOK("Salvar");
					
					gDialog.addListerner(new GenericDialog.DialogListerner() {
						
						@Override
						public void onClose(DialogEvent event) {
							if(event.isConfirm()){
								if(fieldGroup.isValid() && valid_mac){
									try {	
										
										fieldGroup.commit();				
										fireEvent(new EditarRegimeComodatoParaComodatoTotalEditorEvent(getUI(), item,motivo,null, true));
											
									} catch (Exception e) {										
										e.printStackTrace();
										Notify.Show("ERRO: "+e.getLocalizedMessage(), Notify.TYPE_ERROR);
									}
								}else{
									Notify.Show_Invalid_Submit_Form();
								}
							}else{							
								fieldGroup.discard();				
								fireEvent(new EditarRegimeComodatoParaComodatoTotalEditorEvent(getUI(), item,motivo,null, false));
								close();						
							}
						}
					});					
					
					getUI().addWindow(gDialog);
					
				}				
			}
		});
		
		ShortcutListener slbtCancelar = new ShortcutListener("Cancelar", ShortcutAction.KeyCode.ESCAPE,null) {
			
			@Override
			public void handleAction(Object sender, Object target) {
				btCancelar.click();
			}
		};
		
		btCancelar.addShortcutListener(slbtCancelar);
		btCancelar.setEnabled(true);
		
		return btCancelar;
	}
	
	
	
	public void addListerner(EditarRegimeComodatoParaComodatoTotalEditorListerner target){
		try {
			Method method = EditarRegimeComodatoParaComodatoTotalEditorListerner.class.getDeclaredMethod("onClose", EditarRegimeComodatoParaComodatoTotalEditorEvent.class);
			addListener(EditarRegimeComodatoParaComodatoTotalEditorEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("M??todo n??o Encontrado!");
		}
	}
	public void removeListerner(EditarRegimeComodatoParaComodatoTotalEditorListerner target){
		removeListener(EditarRegimeComodatoParaComodatoTotalEditorEvent.class, target);
	}
	public static class EditarRegimeComodatoParaComodatoTotalEditorEvent extends Event{
		
		private Item item;
		private boolean confirm;
		private String motivo;
		
		private Integer codSerialOnu;
		
		public EditarRegimeComodatoParaComodatoTotalEditorEvent(Component source, Item item,String motivo,Integer codSerialOnu, boolean confirm) {
			super(source);
			this.item = item;
			this.motivo = motivo;
			this.confirm = confirm;			
			this.codSerialOnu = codSerialOnu;			
		}
		
		public Integer getCodSerialOnu(){
			return codSerialOnu;
		}

		public Item getItem() {
			return item;
		}	
		
		public String getMotivo() {
			return motivo;
		}	

		public boolean isConfirm() {
			return confirm;
		}	
		
		
	}
	public interface EditarRegimeComodatoParaComodatoTotalEditorListerner extends Serializable{
		public void onClose(EditarRegimeComodatoParaComodatoTotalEditorEvent event);
	}

}

