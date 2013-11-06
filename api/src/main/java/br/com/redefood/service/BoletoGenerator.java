package br.com.redefood.service;

import java.io.File;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jrimum.bopepo.BancosSuportados;
import org.jrimum.bopepo.Boleto;
import org.jrimum.bopepo.view.BoletoViewer;
import org.jrimum.domkee.comum.pessoa.endereco.CEP;
import org.jrimum.domkee.comum.pessoa.endereco.Endereco;
import org.jrimum.domkee.comum.pessoa.endereco.UnidadeFederativa;
import org.jrimum.domkee.financeiro.banco.febraban.Agencia;
import org.jrimum.domkee.financeiro.banco.febraban.Carteira;
import org.jrimum.domkee.financeiro.banco.febraban.Cedente;
import org.jrimum.domkee.financeiro.banco.febraban.ContaBancaria;
import org.jrimum.domkee.financeiro.banco.febraban.NumeroDaConta;
import org.jrimum.domkee.financeiro.banco.febraban.Sacado;
import org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo;
import org.jrimum.domkee.financeiro.banco.febraban.Titulo;
import org.jrimum.domkee.financeiro.banco.febraban.Titulo.Aceite;

import br.com.redefood.model.Account;
import br.com.redefood.model.RedeFoodData;
import br.com.redefood.model.Subsidiary;
import br.com.redefood.util.LocaleResource;
import br.com.redefood.util.RedeFoodAnswerGenerator;

public class BoletoGenerator {

	// TODO: finish this, ADD ACCOUNT para poder descrever o que está sendo
	// cobrado, e pegar o valor
	@SuppressWarnings("unused")
	public String generateBankSlip(Subsidiary subsidiary,
			List<Account> accounts, RedeFoodData rf) {
		// log.log(Level.INFO, "Generating bank slip to subsidiary " +
		// subsidiary.getId());
		try {
			/*
			 * INFORMANDO DADOS SOBRE O CEDENTE(Cobrador).
			 */
			Cedente cedente = new Cedente(rf.getName(), rf.getCnpj());

			/*
			 * INFORMANDO DADOS SOBRE O SACADO(Cobrado).
			 */
			Sacado sacado = new Sacado(subsidiary.getName(),
					subsidiary.getCnpj());

			// Informando o endereço do sacado.
			Endereco enderecoSac = new Endereco();

			enderecoSac.setUF(UnidadeFederativa.valueOfSigla(subsidiary
					.getAddress().getCity().getState().getShortName()));
			enderecoSac.setLocalidade(subsidiary.getAddress().getCity()
					.getName());
			enderecoSac.setCep(new CEP(subsidiary.getAddress().getZipcode()));
			enderecoSac.setBairro(subsidiary.getAddress().getNeighborhood()
					.getName());
			enderecoSac.setLogradouro(subsidiary.getAddress().getStreet());
			enderecoSac.setNumero(subsidiary.getAddress().getNumber());
			sacado.addEndereco(enderecoSac);

			/*
			 * INFORMANDO DADOS SOBRE O SACADOR AVALISTA. SacadorAvalista
			 * sacadorAvalista = new SacadorAvalista("RedeFood",
			 * "00.000.000/0001-91");
			 * 
			 * // Informando o endereço do sacador avalista. Endereco
			 * enderecoSacAval = new Endereco();
			 * enderecoSacAval.setUF(UnidadeFederativa.DF);
			 * enderecoSacAval.setLocalidade("Brasília");
			 * enderecoSacAval.setCep(new CEP("59000-000"));
			 * enderecoSacAval.setBairro("Grande Centro");
			 * enderecoSacAval.setLogradouro("Rua Eternamente Principal");
			 * enderecoSacAval.setNumero("001");
			 * sacadorAvalista.addEndereco(enderecoSacAval);
			 */

			/*
			 * INFORMANDO OS DADOS SOBRE O TÍTULO.
			 */

			// Informando dados sobre a conta bancária do título.
			ContaBancaria contaBancaria = new ContaBancaria(
					BancosSuportados.BANCO_DO_BRASIL.create());
			contaBancaria.setNumeroDaConta(new NumeroDaConta(Integer.valueOf(rf
					.getAccountNumber().split("-")[0].replace(".", "")), rf
					.getAccountNumber().split("-")[1]));
			contaBancaria.setCarteira(new Carteira(18));
			contaBancaria.setAgencia(new Agencia(Integer.valueOf(rf
					.getBankAgency()), rf.getBankAgencyDigit()));

			Titulo titulo = new Titulo(contaBancaria, sacado, cedente);
			// TODO: precisa salvar no banco o nº do ultimo boleto gerado, além
			// de salvar o nº do boleto pra cada conta de cada loja
			titulo.setNumeroDoDocumento("123456");
			titulo.setNossoNumero("16002590004" + titulo.getNumeroDoDocumento());
			// titulo.setDigitoDoNossoNumero("5");
			titulo.setValor(BigDecimal.valueOf(1.00));
			Date today = new Date();
			titulo.setDataDoDocumento(today);
			titulo.setDataDoVencimento(deadLine(8));
			titulo.setTipoDeDocumento(TipoDeTitulo.DM_DUPLICATA_MERCANTIL);
			titulo.setAceite(Aceite.N);
			titulo.setDesconto(BigDecimal.ZERO);
			titulo.setDeducao(BigDecimal.ZERO);
			titulo.setMora(BigDecimal.ZERO);
			titulo.setAcrecimo(BigDecimal.ZERO);
			titulo.setValorCobrado(titulo.getValor());

			/*
			 * INFORMANDO OS DADOS SOBRE O BOLETO.
			 */
			Boleto boleto = new Boleto(titulo);

			boleto.setLocalPagamento("Pagável em qualquer Banco até a data de vencimento.");
			// boleto.setInstrucaoAoSacado("Senhor sacado, sabemos sim que o valor "
			// + "cobrado não é o esperado, aproveite o DESCONTÃO!");
			boleto.setInstrucao1("Para gerar uma segunda via, favor acessar www.admin.redefood.com.br");
			boleto.setInstrucao2("Este boleto será protestado automaticamente 5 dias após seu vencimento.");

			/*
			 * GERANDO O BOLETO BANCÁRIO.
			 */
			// Instanciando um objeto "BoletoViewer", classe responsável pela
			// geração do boleto bancário.
			BoletoViewer boletoViewer = new BoletoViewer(boleto);

			// Gerando o arquivo. No caso o arquivo mencionado será salvo na
			// mesma
			// pasta do projeto. Outros exemplos:
			// WINDOWS: boletoViewer.getAsPDF("C:/Temp/MeuBoleto.pdf");
			// LINUX: boletoViewer.getAsPDF("/home/temp/MeuBoleto.pdf");
			File arquivoPdf = boletoViewer
					.getPdfAsFile("/home/pulu/MeuBoleto.pdf");

			// Mostrando o boleto gerado na tela.
			// mostreBoletoNaTela(arquivoPdf);
			// log.log(Level.INFO, "Bank slip generated to subsidiary " +
			// subsidiary.getId());

			return "boleto gerado, download, enviado, email, sei lá, algo assim";
		} catch (Exception e) {
			if (e.getMessage().contentEquals("failed to find redefood")) {
				String answer = LocaleResource.getProperty("pt_br")
						.getProperty("exception.find.data");
				// log.log(Level.SEVERE, answer);
				return RedeFoodAnswerGenerator.generateErrorAnswerString(500,
						answer);
			}
		}
		String answer = LocaleResource.getProperty("pt_br").getProperty(
				"exception.generic");
		// log.log(Level.SEVERE, answer);
		return RedeFoodAnswerGenerator.generateErrorAnswerString(500, answer);
	}

	private Date deadLine(int days) {
		Calendar lastDay = Calendar.getInstance();
		lastDay.add(Calendar.DAY_OF_MONTH, days);
		return lastDay.getTime();
	}

	/**
	 * Exibe o arquivo na tela.
	 * 
	 * @param arquivoBoleto
	 * 
	 *            private static void mostreBoletoNaTela(File arquivoBoleto) {
	 * 
	 *            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
	 * 
	 *            try { desktop.open(arquivoBoleto); } catch (IOException e) {
	 *            e.printStackTrace(); } }
	 */
}
