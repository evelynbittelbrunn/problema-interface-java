package model.services;

import java.time.Duration;

import model.entities.CarRental;
import model.entities.Invoice;

public class RentalService {
	private Double pricePerDay;
	private Double pricePerHour;
	
	private taxService taxService;

	// Aqui fazemos uma injeção de dependência (instância dentro do construtor).
	// Mantemos a instância de TaxService por ser mais genérica.
	// Se especificássemos que deve receber uma instância de BrazilianTaxService, geraria um forte acoplamento
	// e causaria mais pontos de alterações caso fosse necessária uma mudança.
	public RentalService(Double pricePerDay, Double pricePerHour, taxService taxService) {
		this.pricePerDay = pricePerDay;
		this.pricePerHour = pricePerHour;
		this.taxService = taxService;
	}
	
	public void processInvoice(CarRental carRental) {
		
		double minutes = Duration.between(carRental.getStart(), carRental.getFinish()).toMinutes();
		double hours = minutes / 60.0;
		
		double basicPayment = hours <= 12.0 
				? pricePerHour * Math.ceil(hours) 
				: pricePerDay * Math.ceil(hours / 24.0);
		
		double tax = taxService.tax(basicPayment);
		
		carRental.setInvoice(new Invoice(basicPayment, tax));
	}
	
}
