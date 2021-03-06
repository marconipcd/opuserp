package com.digital.opuserp.view.modulos.financeiro.contasReceber;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.digital.opuserp.OpusERP4UI;
import com.digital.opuserp.dao.ContasReceberDAO;
import com.digital.opuserp.dao.GerenciarModuloDAO;
import com.digital.opuserp.domain.ContasReceber;
import com.digital.opuserp.domain.RegistroLiquidado;
import com.digital.opuserp.util.ConnUtil;
import com.digital.opuserp.util.GenericDialog;
import com.digital.opuserp.util.GenericDialog.DialogEvent;
import com.digital.opuserp.util.Real;
import com.digital.opuserp.view.modulos.cadastro.veiculos.VeiculosEditor.VeiculoEvent;
import com.digital.opuserp.view.modulos.cadastro.veiculos.VeiculosEditor.VeiculoListerner;
import com.digital.opuserp.view.util.Notify;
import com.vaadin.data.Property;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ConfirBaixaArqRet extends Window {

//	JPAContainer<Ose> container;
	
	TextField tfBusca;
	Table tb;
	Button btBaixar;
	Button btCancelar;
	
	Integer codCliente;
	Integer qtdRegistros = 0;
	private Label lbRegistros;
	HorizontalLayout hlFloatTb;
	HorizontalLayout hlFloat;
	
	GerenciarModuloDAO gmDAO;
	private Integer codSubModulo;
	private String nomeArquivo;
	private boolean confirBaixa;
	private String status;
	
	private String banco = "";
	
	private boolean desabilitarBaixar =false;
	
	VerticalLayout vlContent;
	
	public ConfirBaixaArqRet(boolean modal, boolean center,String nomeArquivo, String banco){
		
		super("Confirmar Baixa Arquivo Retorno");
		this.nomeArquivo = nomeArquivo;
		this.banco = banco;
		
		setWidth("956px");
		setHeight("540px");
		
		
		setModal(modal);
		if(center){
			center();
		}
		
		setClosable(false);

		gmDAO = new GerenciarModuloDAO();
		
		final VerticalLayout vlTabelas = new VerticalLayout();
		vlTabelas.setSizeFull();
		vlTabelas.addComponent(buildTbGeneric());
		if(regList_recusados != null && regList_recusados.size() > 0){
			vlTabelas.addComponent(buildTbRecusados(regList_recusados));
		}
		
		
		vlContent = new VerticalLayout(){
			{
				setSizeFull();
				setMargin(true);
				addComponent(vlTabelas);
				
				setExpandRatio(vlTabelas, 2);
				
				HorizontalLayout hlButtons = new HorizontalLayout();
				hlButtons.setSpacing(true);
				hlButtons.setMargin(true);
				hlButtons.setStyleName("hl_buttons_bottom");
				hlButtons.addComponent(buildBtBaixar());
				hlButtons.addComponent(buildBtCancelar());
				
				if(desabilitarBaixar){
					btBaixar.setEnabled(false); 
				}
				
				
				hlFloat = new HorizontalLayout();
				hlFloat.setWidth("100%");
				hlFloat.addComponent(BuildLbRegistros(qtdRegistros));
				hlFloat.setComponentAlignment(lbRegistros, Alignment.BOTTOM_LEFT);
				hlFloat.addComponent(hlButtons);
				hlFloat.setComponentAlignment(hlButtons, Alignment.BOTTOM_RIGHT);
				
				addComponent(hlFloat);
			}
		};
		
		
		setContent(vlContent);
		
		if(tb.getContainerDataSource().getItemIds().size() == 0){
			fireEvent(new BaixaEvent(this));
		}
		
	}
	
	public Label BuildLbRegistros(Integer qtd){
		
		
		if(regList_recusados != null && regList_recusados.size() > 0){
			
			String r = new String().valueOf(regList_recusados.size())+" Recusados";
			
			lbRegistros = new Label(qtd.toString() + " Liquidados | "+r);
		}else{
			lbRegistros = new Label(qtd.toString() + " Liquidados");

		}
		
		
		
		return lbRegistros;
	}
	
	public Table buildTbRecusados(List<RegistroLiquidado> result){
		Table tb2 = new Table("RECUSADOS");
		tb2.setSizeFull();
	
		tb2.addContainerProperty("N.Numero", String.class, "");
		tb2.addContainerProperty("Ocorrencia", String.class, "");
		tb2.addContainerProperty("Motivo", String.class, "");			

		Integer i = 1;
		for(RegistroLiquidado r : result){	 
			
					StringBuilder sb2 = new StringBuilder(r.getNosso_numero());
					sb2.insert(2, "/");
					sb2.insert(9, "-");
					
					String motivo = "";
					
					switch (r.getDesc_comando().substring(0, 2)) {
					
						case "01":
							motivo = "C??digo do banco inv??lido";
							break;
							
						case "02":
							motivo = "C??digo do registro detalhe inv??lido";
							break;
							
						case "03":
							motivo = "C??digo da ocorr??ncia inv??lido";
							break;
							
						case "04":
							motivo = "C??digo de ocorr??ncia n??o permitida para a carteira";
							break;
							
						case "05":
							motivo = "C??digo de ocorr??ncia n??o num??rico";
							break;
							
						case "07":
							motivo = "Cooperativa/ag??ncia/conta/d??gito inv??lidos";
							break;
							
						case "08":
							motivo = "Nosso n??mero inv??lido";
							break;
							
						case "09":
							motivo = "Nosso n??mero duplicado";
							break;
							
						case "10":
							motivo = "Carteira inv??lida";
							break;
							
						case "14":
							motivo = "T??tulo protestado";
							break;
							
						case "15":
							motivo = "Cooperativa/carteira/ag??ncia/conta/nosso n??mero inv??lidos";
							break;
							
						case "16":
							motivo = "Data de vencimento inv??lida";
							break;
							
						case "17":
							motivo = "Data de vencimento anterior ?? data de emiss??o";
							break;
							
						case "18":
							motivo = "Vencimento fora do prazo de opera????o";
							break;
							
						case "20":
							motivo = "Valor do t??tulo inv??lido";
							break;
							
						case "21":
							motivo = "Esp??cie do t??tulo inv??lida";
							break;
							
						case "22":
							motivo = "Esp??cie n??o permitida para a carteira";
							break;
							
						case "24":
							motivo = "Data de emiss??o inv??lida";
							break;
							
						case "29":
							motivo = "Valor do desconto maior/igual ao valor do t??tulo";
							break;
							
						case "31":
							motivo = "Concess??o de desconto - existe desconto anterior";
							break;
							
						case "33":
							motivo = "Valor do abatimento inv??lido";
							break;
							
						case "34":
							motivo = "Valor do abatimento maior/igual ao valor do t??tulo";
							break;
							
						case "36":
							motivo = "Concess??o de abatimento - existe abatimento anterior";
							break;
							
						case "38":
							motivo = "Prazo para protesto inv??lido";
							break;
							
						case "39":
							motivo = "Pedido para protesto n??o permitido para o t??tulo";
							break;
							
						case "40":
							motivo = "T??tulo com ordem de protesto emitida";
							break;
							
						case "41":
							motivo = "Pedido cancelamento/susta????o sem instru????o de protesto";
							break;
							
						case "44":
							motivo = "Cooperativa de cr??dito/ag??ncia benefici??ria n??o prevista";
							break;
							
						case "45":
							motivo = "Nome do pagador inv??lido";
							break;
							
						case "46":
							motivo = "Tipo/n??mero de inscri????o do pagador inv??lidos";
							break;
							
						case "47":
							motivo = "Endere??o do pagador n??o informado";
							break;
							
						case "48":
							motivo = "CEP irregular";
							break;
						
						case "49":
							motivo = "N??mero de Inscri????o do pagador/avalista inv??lido";
							break;
							
						case "50":
							motivo = "Pagador/avalista n??o informado";
							break;
							
						case "60":
							motivo = "Movimento para t??tulo n??o cadastrado";
							break;
							
						case "63":
							motivo = "Entrada para t??tulo j?? cadastrado";
							break;
							
						case "A":
							motivo = "Aceito";
							break;
							
						case "D":
							motivo = "Desprezado";
							break;
							
						case "A1":
							motivo = "Pra??a do pagador n??o cadastrada.";
							break;
							
						case "A2":
							motivo = "Tipo de cobran??a do t??tulo divergente com a pra??a do pagador.";
							break;
							
						case "A3":
							motivo = "Cooperativa/ag??ncia deposit??ria divergente: atualiza o cadastro de pra??as da Coop./ag??ncia benefici??ria";
							break;
							
						case "A4":
							motivo = "Benefici??rio n??o cadastrado ou possui CGC/CIC inv??lido";
							break;
							
						case "A5":
							motivo = "Pagador n??o cadastrado";
							break;
							
						case "A6":
							motivo = "Data da instru????o/ocorr??ncia inv??lida";
							break;
							
						case "A7":
							motivo = "Ocorr??ncia n??o pode ser comandada";
							break;
							
						case "A8":
							motivo = "Recebimento da liquida????o fora da rede Sicredi - via compensa????o eletr??nica";
							break;
							
						case "B4":
							motivo = "Tipo de moeda inv??lido";
							break;
							
						case "B5":
							motivo = "Tipo de desconto/juros inv??lido";
							break;
							
						case "B6":
							motivo = "Mensagem padr??o n??o cadastrada";
							break;
							
						case "B7":
							motivo = "Seu n??mero inv??lido";
							break;
						
						case "B8":
							motivo = "Percentual de multa inv??lido";
							break;
						
						case "B9":
							motivo = "Valor ou percentual de juros inv??lido";
							break;
							
						case "C1":
							motivo = "Data limite para concess??o de desconto inv??lida";
							break;
							
						case "C2":
							motivo = "Aceite do t??tulo inv??lido";
							break;
							
						case "C3":
							motivo = "Campo alterado na instru????o ???31 ??? altera????o de outros dados??? inv??lido";
							break;
							
						case "C4":
							motivo = "T??tulo ainda n??o foi confirmado pela centralizadora";
							break;
							
						case "C5":
							motivo = "T??tulo rejeitado pela centralizadora";
							break;
							
						case "C6":
							motivo = "T??tulo j?? liquidado";
							break;
							
						case "C7":
							motivo = "T??tulo j?? baixado";
							break;
							
						case "C8":
							motivo = "Existe mesma instru????o pendente de confirma????o para este t??tulo";
							break;
							
						case "C9":
							motivo = "Instru????o pr??via de concess??o de abatimento n??o existe ou n??o confirmada";
							break;
							
						case "D1":
							motivo = "T??tulo dentro do prazo de vencimento (em dia)";
							break;
							
						case "D2":
							motivo = "Esp??cie de documento n??o permite protesto de t??tulo";
							break;
							
						case "D3":
							motivo = "T??tulo possui instru????o de baixa pendente de confirma????o";
							break;
							
						case "D4":
							motivo = "Quantidade de mensagens padr??o excede o limite permitido";
							break;
							
						case "D5":
							motivo = "Quantidade inv??lida no pedido de boletos pr??-impressos da cobran??a sem registro";
							break;
							
						case "D6":
							motivo = "Tipo de impress??o inv??lida para cobran??a sem registro";
							break;
							
						case "D7":
							motivo = "Cidade ou Estado do pagador n??o informado";
							break;
							
						case "D8":
							motivo = "Seq????ncia para composi????o do nosso n??mero do ano atual esgotada";
							break;
							
						case "D9":
							motivo = "Registro mensagem para t??tulo n??o cadastrado";
							break;
							
						case "E2":
							motivo = "Registro complementar ao cadastro do t??tulo da cobran??a com e sem registro n??o cadastrado";
							break;
							
						case "E3":
							motivo = "Tipo de postagem inv??lido, diferente de S, N e branco";
							break;
							
						case "E4":
							motivo = "Pedido de boletos pr??-impressos";
							break;
							
						case "E5":
							motivo = "Confirma????o/rejei????o para pedidos de boletos n??o cadastrado";
							break;
							
						case "E6":
							motivo = "Pagador/avalista n??o cadastrado";
							break;
							
						case "E7":
							motivo = "Informa????o para atualiza????o do valor do t??tulo para protesto inv??lido";
							break;
							
						case "E8":
							motivo = "Tipo de impress??o inv??lido, diferente de A, B e branco";
							break;
							
						case "E9":
							motivo = "C??digo do pagador do t??tulo divergente com o c??digo da cooperativa de cr??dito";
							break;
							
						case "F1":
							motivo = "Liquidado no sistema do cliente";
							break;
							
						case "F2":
							motivo = "Baixado no sistema do cliente";
							break;
							
						case "F3":
							motivo = "Instru????o inv??lida, este t??tulo est?? caucionado/descontado";
							break;
							
						case "F4":
							motivo = "Instru????o fixa com caracteres inv??lidos";
							break;
							
						case "F6":
							motivo = "Nosso n??mero / n??mero da parcela fora de seq????ncia ??? total de parcelas inv??lido";
							break;
							
						case "F7":
							motivo = "Falta de comprovante de presta????o de servi??o";
							break;
							
						case "F8":
							motivo = "Nome do benefici??rio incompleto / incorreto.";
							break;
							
						case "F9":
							motivo = "CNPJ / CPF incompat??vel com o nome do pagador / Sacador Avalista";
							break;
							
						case "G1":
							motivo = "CNPJ / CPF do pagador Incompat??vel com a esp??cie";
							break;
							
						case "G2":
							motivo = "T??tulo aceito: sem a assinatura do pagador";
							break;
							
						case "G3":
							motivo = "T??tulo aceito: rasurado ou rasgado";
							break;
							
						case "G4":
							motivo = "T??tulo aceito: falta t??tulo (cooperativa/ag. benefici??ria dever?? envi??-lo)";
							break;
							
						case "G5":
							motivo = "Pra??a de pagamento incompat??vel com o endere??o";
							break;
							
						case "G6":
							motivo = "T??tulo aceito: sem endosso ou benefici??rio irregular";
							break;
							
						case "G7":
							motivo = "T??tulo aceito: valor por extenso diferente do valor num??rico";
							break;
							
						case "G8":
							motivo = "Saldo maior que o valor do t??tulo";
							break;
							
						case "G9":
							motivo = "Tipo de endosso inv??lido";
							break;
							
						case "H1":
							motivo = "Nome do pagador incompleto / Incorreto";
							break;
							
						case "H2":
							motivo = "Susta????o judicial";
							break;
							
						case "H3":
							motivo = "Pagador n??o encontrado";
							break;
							
						case "H4":
							motivo = "Altera????o de carteira";
							break;
							
						case "H5":
							motivo = "Recebimento de liquida????o fora da rede Sicredi ??? VLB Inferior ??? Via Compensa????o";
							break;
							
						case "H6":
							motivo = "Recebimento de liquida????o fora da rede Sicredi ??? VLB Superior ??? Via Compensa????o";
							break;
							
						case "H7":
							motivo = "Esp??cie de documento necessita benefici??rio ou avalista PJ";
							break;
							
						case "H8":
							motivo = "Recebimento de liquida????o fora da rede Sicredi ??? Conting??ncia Via Compe";
							break;
							
						case "H9":
							motivo = "Dados do t??tulo n??o conferem com disquete";
							break;
							
						case "I1":
							motivo = "Pagador e Sacador Avalista s??o a mesma pessoa";
							break;
							
						case "I2":
							motivo = "Aguardar um dia ??til ap??s o vencimento para protestar";
							break;
							
						case "I3":
							motivo = "Data do vencimento rasurada";
							break;
							
						case "I4":
							motivo = "Vencimento ??? extenso n??o confere com n??mero";
							break;
							
						case "I5":
							motivo = "Falta data de vencimento no t??tulo";
							break;
							
						case "I6":
							motivo = "DM/DMI sem comprovante autenticado ou declara????o";
							break;
							
						case "I7":
							motivo = "Comprovante ileg??vel para confer??ncia e microfilmagem";
							break;
							
						case "I8":
							motivo = "Nome solicitado n??o confere com emitente ou pagador";
							break;
						
						case "I9":
							motivo = "Confirmar se s??o 2 emitentes. Se sim, indicar os dados dos 2";
							break;
							
						case "J1":
							motivo = "Endere??o do pagador igual ao do pagador ou do portador";
							break;
							
						case "J2":
							motivo = "Endere??o do apresentante incompleto ou n??o informado";
							break;
							
						case "J3":
							motivo = "Rua/n??mero inexistente no endere??o";
							break;
							
						case "J4":
							motivo = "Falta endosso do favorecido para o apresentante";
							break;
							
						case "J5":
							motivo = "Data da emiss??o rasurada";
							break;
							
						case "J6":
							motivo = "Falta assinatura do pagador no t??tulo";
							break;
							
						case "J7":
							motivo = "Nome do apresentante n??o informado/incompleto/incorreto";
							break;
							
						case "J8":
							motivo = "Erro de preenchimento do titulo";
							break;
							
						case "J9":
							motivo = "Titulo com direito de regresso vencido";
							break;
							
						case "K1":
							motivo = "Titulo apresentado em duplicidade";
							break;
							
						case "K2":
							motivo = "Titulo j?? protestado";
							break;
							
						case "K3":
							motivo = "Letra de cambio vencida ??? falta aceite do pagador";
							break;
							
						case "K4":
							motivo = "Falta declara????o de saldo assinada no t??tulo";
							break;
							
						case "K5":
							motivo = "Contrato de cambio ??? Falta conta gr??fica";
							break;
							
						case "K6":
							motivo = "Aus??ncia do documento f??sico";
							break;
							
						case "K7":
							motivo = "Pagador falecido";
							break;
							
						case "K8":
							motivo = "Pagador apresentou quita????o do t??tulo";
							break;
							
						case "K9":
							motivo = "Pagador apresentou quita????o do t??tulo";
							break;
							
						case "L1":
							motivo = "T??tulo com emiss??o anterior a concordata do pagador";
							break;
							
						case "L2":
							motivo = "Pagador consta na lista de fal??ncia";
							break;
							
						case "L3":
							motivo = "Apresentante n??o aceita publica????o de edital";
							break;
							
						case "L4":
							motivo = "Dados do Pagador em Branco ou inv??lido";
							break;
							
						case "L5":
							motivo = "C??digo do Pagador na ag??ncia benefici??ria est?? duplicado";
							break;
							
						case "M1":
							motivo = "Reconhecimento da d??vida pelo pagador";
							break;
							
						case "M2":
							motivo = "N??o reconhecimento da d??vida pelo pagador";
							break;
							
						case "M3":
							motivo = "Inclus??o de desconto 2 e desconto 3 inv??lida";
							break;
						
						case "X1":
							motivo = "Regulariza????o centralizadora ??? Rede Sicredi";
							break;
							
						case "X2":
							motivo = "Regulariza????o centralizadora ??? Compensa????o";
							break;
						
						case "X3":
							motivo = "Regulariza????o centralizadora ??? Banco correspondente";
							break;
							
						case "X4":
							motivo = "Regulariza????o centralizadora - VLB Inferior - via compensa????o";
							break;
							
						case "X5":
							motivo = "Regulariza????o centralizadora - VLB Superior - via compensa????o";
							break;
							
						case "X0":
							motivo = "Pago com cheque";
							break;
							
						case "X6":
							motivo = "Pago com cheque ??? bloqueado 24 horas";
							break;
							
						case "X7":
							motivo = "Pago com cheque ??? bloqueado 48 horas";
							break;
							
						case "X8":
							motivo = "Pago com cheque ??? bloqueado 72 horas";
							break;
							
						case "X9":
							motivo = "Pago com cheque ??? bloqueado 96 horas";
							break;
							
						case "XA":
							motivo = "Pago com cheque ??? bloqueado 120 horas";
							break;
							
						case "XB":
							motivo = "Pago com cheque ??? bloqueado 144 horas";
							break;
	
						default:
							motivo = r.getDesc_comando();
							break;
						}
						
						
						String ocorrencia = "";
						
						switch (r.getComando()) {
							case "02":
								ocorrencia = "Entrada confirmada";
								break;
								
							case "03":
								ocorrencia = "Entrada rejeitada";
								break;
								
							case "06":
								ocorrencia = "Liquida????o normal";
								break;
								
							case "09":
								ocorrencia = "Baixado automaticamente via arquivo";
								break;
								
							case "10":
								ocorrencia = "Baixado conforme instru????es da cooperativa de cr??dito";
								break;
								
							case "12":
								ocorrencia = "Abatimento concedido";
								break;
								
							case "13":
								ocorrencia = "Abatimento cancelado";
								break;
								
							case "14":
								ocorrencia = "Vencimento alterado";
								break;
								
							case "15":
								ocorrencia = "Liquida????o em cart??rio";
								break;
								
							case "17":
								ocorrencia = "Liquida????o ap??s baixa";
								break;
								
							case "19":
								ocorrencia = "Confirma????o de recebimento de instru????o de protesto";
								break;
								
							case "20":
								ocorrencia = "Confirma????o de recebimento de instru????o de susta????o de protesto";
								break;
								
							case "23":
								ocorrencia = "Entrada de t??tulo em cart??rio";
								break;
								
							case "24":
								ocorrencia = "Entrada rejeitada por CEP irregular";
								break;
								
							case "27":
								ocorrencia = "Baixa rejeitada";
								break;
								
							case "28":
								ocorrencia = "Tarifa";
								break;
								
							case "29":
								ocorrencia = "Rejei????o do pagador";
								break;
								
							case "30":
								ocorrencia = "Altera????o rejeitada";
								break;
								
							case "32":
								ocorrencia = "Instru????o rejeitada";
								break;
								
							case "33":
								ocorrencia = "Confirma????o de pedido de altera????o de outros dados";
								break;
								
							case "34":
								ocorrencia = "Retirado de cart??rio e manuten????o em carteira";
								break;
								
							case "35":
								ocorrencia = "Aceite do pagador";
								break;
		
							default:
								ocorrencia = r.getComando();
						}
					
					
					tb2.addItem(new Object[]{sb2.toString(),ocorrencia,motivo}, i);
					i++;
					//qtdRegistros++;
				
		}
		
		tb.setCaption("LIQUIDADOS");
		
		return tb2;
	}

	List<RegistroLiquidado> regList_recusados = null;
	
	public Table buildTbGeneric() {
		
		tb = new Table(){
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
								
				
				if(tb.getType(colId).equals(Date.class)){
					
					if(tb.getItem(rowId).getItemProperty(colId).getValue() != null){
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						
						String data = tb.getItem(rowId).getItemProperty(colId).getValue().toString();
						return sdf.format(tb.getItem(rowId).getItemProperty(colId).getValue());
					}else{
						return null;
					}
					
				}else{
					if(tb.getItem(rowId).getItemProperty(colId).getValue() != null && !tb.getItem(rowId).getItemProperty(colId).getValue().equals("")){
						return super.formatPropertyValue(rowId, colId, property);
					}else{
						return null;						
					}
				}
			}
		};
		
//		tb.setSelectable(true);
		tb.setColumnCollapsingAllowed(true);
		tb.setSizeFull();
//		tb.setMultiSelect(true);
//		tb.setMultiSelectMode(MultiSelectMode.DEFAULT);
		
		tb.addContainerProperty("Cod.", Integer.class, "");
		tb.addContainerProperty("N.Numero", String.class, "");
		tb.addContainerProperty("Valor Pago", String.class, "");
		tb.addContainerProperty("Data Vencimento", String.class, "");
		tb.addContainerProperty("Data Pagamento", String.class, "");
		tb.addContainerProperty("Status", String.class, "");
		tb.addContainerProperty("Empresa", String.class, "");
		tb.addContainerProperty("Data Cadrastro", Date.class, null);
			
		
		tb.setCellStyleGenerator(new Table.CellStyleGenerator() {

            @Override
            public String getStyle(Table source, Object itemId, Object propertyId) {

	            status = source.getItem(itemId).getItemProperty("Status").getValue().toString();
	            
	            if(status.equals("ABERTO")){
	            	return "row-header-default"; 
	            }else{
	            	return "row-header-crm-atrasado"; 		                
	            }
            }

        });
		
		tb.setColumnWidth("Cod.", 60);
		tb.setColumnWidth("N.Numero", 130);
		tb.setColumnWidth("Valor Pago", 80);
		tb.setColumnWidth("Data Vencimento", 110);
		tb.setColumnWidth("Data Pagamento", 110);
		tb.setColumnWidth("Status", 100);
		tb.setColumnWidth("Empresa", 80);
		tb.setColumnWidth("Data Cadrastro", 150);
		
		tb.setVisibleColumns(new Object[]{"Cod.","N.Numero","Valor Pago","Data Vencimento","Data Pagamento","Status","Empresa","Data Cadrastro"});

		tb.setImmediate(true);
		
		EntityManager emr = ConnUtil.getEntity();
		
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();	
		
		List<RegistroLiquidado> regList = null;
		
		try{
			File arquivo = new File(basepath + "//WEB-INF//uploads//" +nomeArquivo);
			List<String> lines = FileUtils.readLines(arquivo, "UTF8");
			
			if(lines.size() > 0 && lines.get(0).length() == 400){
				
				if(banco.equals("BB")){
					regList = ContasReceberDAO.liquidarBoletosPadraoCnab400(new File(basepath + "//WEB-INF//uploads//" +nomeArquivo));
				}
				
				if(banco.equals("SICRED")){
					regList = ContasReceberDAO.liquidarBoletosPadraoCnab400Sicred(new File(basepath + "//WEB-INF//uploads//" +nomeArquivo), "liquidados");
					regList_recusados = ContasReceberDAO.liquidarBoletosPadraoCnab400Sicred(new File(basepath + "//WEB-INF//uploads//" +nomeArquivo), "recusados");
				}
				
				
				System.out.println("Arquivo Padr??o CNAB400");
				
				if(regList != null && regList.size() == 0){
					Notify.Show("Nenhum registro encontrado!", Notify.TYPE_NOTICE);
					this.close();
				}
			}else{
				regList = ContasReceberDAO.liquidarBoletosPadraoAntigo(new File(basepath + "//WEB-INF//uploads//" +nomeArquivo));
				System.out.println("Arquivo Padr??o CBR463");
				
				if(regList.size() == 0){
					Notify.Show("Nenhum registro encontrado!", Notify.TYPE_NOTICE);
					this.close();
				}
			}
		}catch(Exception e){
			System.out.println("Arquivo n??o localizado!");
		}
		
		ContasReceberDAO crDAO = new ContasReceberDAO();
		
		List<RegistroLiquidado> result = new ArrayList<>();	
		
		try {
			if(regList!=null && regList.size() > 0){
				for(RegistroLiquidado reg : regList){				
										
					Query q = emr.createQuery("select s from RegistroLiquidado s where s.empresa =:Empresa and s.nosso_numero=:Nnumero", RegistroLiquidado.class);
					q.setParameter("Empresa", OpusERP4UI.getEmpresa().getId().toString());
					q.setParameter("Nnumero", reg.getNosso_numero());
					
					if(q.getResultList().size() == 1){
						result.add((RegistroLiquidado)q.getSingleResult());													
					}
					
					if(q.getResultList().size() == 2){
						result.add((RegistroLiquidado)q.getResultList().get(0));
						result.add((RegistroLiquidado)q.getResultList().get(1));
					}
				}	
				    
					if(result != null && result.size() > 0){
					Integer i = 0;
					String RowAnterior = "";
					Integer RowAnteriorId = 0;
					Integer parentId = null;
					for(RegistroLiquidado r : result){	 
						
						
						ContasReceber contaRc = null; 
								
						if(banco.equals("SICRED")){
							contaRc =	crDAO.procurarBoletosNdocSicred(r.getNosso_numero());
						}
						
						if(banco.equals("BB")){
							contaRc = crDAO.procurarBoletosNdoc(r.getNosso_numero());
						}
						
						DateTimeFormatter fmt = DateTimeFormat.forPattern("ddMMyyyy");
						DateTime dt = fmt.parseDateTime(r.getData_pago());
						
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
						SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy");
						Date d = sdf.parse(dt.toString());
						String data = output.format(d);
						
						if(RowAnterior != null && r.getId() != null && !r.getId().equals(RowAnteriorId) && contaRc != null){
							RowAnteriorId = r.getId();

							String dataVenc = output.format(contaRc.getData_vencimento());
							
							boolean duplicado = ContasReceberDAO.verificarDuplicidade(r.getNosso_numero());

							if(contaRc != null){
								status = contaRc.getStatus();	
							}else{
								status = "NAO ENCONTRADO";		
								//btBaixar.setEnabled(false); 
								desabilitarBaixar = true;
							}
							
							if(duplicado){
								status = "DUPLICADO";
								//btBaixar.setEnabled(false);
								desabilitarBaixar = true;
							}
							
							String valor_pago = Real.formatStringToDBDouble(r.getValor_pago()) < Real.formatStringToDBDouble(contaRc.getValor_titulo()) ? r.getValor_pago()+" (INCONSISTENTE)" : r.getValor_pago();
							
							StringBuilder sb2 = new StringBuilder(r.getNosso_numero());
							sb2.insert(2, "/");
							sb2.insert(9, "-");
							
							tb.addItem(new Object[]{r.getId(),sb2.toString(),valor_pago,dataVenc, data, status,r.getEmpresa(),r.getData_cadastro()}, i);
							
							i++;
							qtdRegistros++;

						}else{
							if(contaRc == null){
								desabilitarBaixar = true;
								
								String valor_pago = r.getValor_pago();
								
								StringBuilder sb2 = new StringBuilder(r.getNosso_numero());
								sb2.insert(2, "/");
								sb2.insert(9, "-");
								
								tb.addItem(new Object[]{r.getId(),sb2.toString(),valor_pago,null, data, "INEXISTENTE",r.getEmpresa(),r.getData_cadastro()}, i);
								i++;
								qtdRegistros++;
							}
						}
						
					}
				}	
			}else{
				close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		
		return tb;
	}
	
	
	public Button buildBtBaixar() {
		btBaixar = new Button("Baixar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {				

				final GenericDialog gDialog = new GenericDialog("Confirme para Continuar!","Deseja realmente baixar o(s) Arquivos(s) Abertos(s) ?", true,true);
				gDialog.setCaptionCANCEL("N??O");
				gDialog.setCaptionOK("SIM");
				gDialog.addListerner(new GenericDialog.DialogListerner() {

					@Override
					public void onClose(DialogEvent event) {
						if(event.isConfirm()){
							
							
							if(banco.equals("BB")){
								confirBaixa = ContasReceberDAO.baixarLiquidado();
							}
							
							if(banco.equals("SICRED")){
								confirBaixa = ContasReceberDAO.baixarLiquidadoSicred(nomeArquivo); 
							}
							
							
							if(confirBaixa){
							}else{
								Notification.show("Boletos j?? foram Liquidados");						
							}	
							gDialog.close();
							close();	
						}else{
							Notification.show("Opera????o Cancelada");
							gDialog.close();
							close();	
						}
					}
		
				});
				getUI().addWindow(gDialog);
		
			}		
		});
		return btBaixar;
	}	
	
	
	private Button buildBtCancelar() {
		btCancelar = new Button("Cancelar", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				EntityManager em = ConnUtil.getEntity();
				Query q = em.createQuery("select rl from RegistroLiquidado rl where rl.nome_arquivo =:file ", RegistroLiquidado.class);
				q.setParameter("file", nomeArquivo);
				
				em.getTransaction().begin();
				for (RegistroLiquidado registro : (List<RegistroLiquidado>)q.getResultList()) {
					em.remove(registro);
				}
				em.getTransaction().commit();
				
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

	
	public Integer getCodSubModulo() {
		return codSubModulo;
	}



	public void setCodSubModulo(Integer codSubModulo) {
		this.codSubModulo = codSubModulo;
	}
	
	
	public void addListerner(BaixaListerner target){
		try {
			Method method = BaixaListerner.class.getDeclaredMethod("onClose", BaixaEvent.class);
			addListener(BaixaEvent.class, target, method);
		} catch (Exception e) {
			System.out.println("M??todo n??o Encontrado!");
		}
	}
	public void removeListerner(VeiculoListerner target){
		removeListener(VeiculoEvent.class, target);
	}
	public static class BaixaEvent extends Event{
		
		public BaixaEvent(Component source) {
			super(source);		
		}

	}
	public interface BaixaListerner extends Serializable{
		public void onClose(BaixaEvent event);
	}
	
}
