package br.com.redefood.rest;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.xml.bind.JAXBException;

import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.model.Address;
import br.com.redefood.model.City;
import br.com.redefood.model.Neighborhood;
import br.com.redefood.model.Parameter;
import br.com.redefood.model.State;
import br.com.redefood.service.Webservicecep;
import br.com.redefood.util.HibernateMapper;
import br.com.redefood.util.LocaleResource;
import br.com.redefood.util.RedeFoodAnswerGenerator;
import br.com.redefood.util.RedeFoodConstants;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/cep")
@Stateless
public class CepResource extends HibernateMapper {
    
    private static final ObjectMapper mapper = HibernateMapper.getMapper();
    @Inject
    private EntityManager em;
    @Inject
    private Logger log;
    @Inject
    private RedeFoodExceptionHandler eh;
    
    /**
     * Method responsible for return a json containing a full address to a given
     * zipcode.
     * 
     * @param locale
     *            locale
     * @param cep
     *            cep
     * @return {@link String} Address to given cep
     */
    @GET
    @Produces("application/json;charset=UTF8")
    public String findCep(@HeaderParam("locale") String locale, @QueryParam("cep") String cep) {
	
	Webservicecep addressFound = null;
	Address address = new Address();
	
	try {
	    String urlCorreios = (String) em.createNamedQuery(Parameter.FIND_PARAMETER_BY_KEY)
		    .setParameter("key", "urlCorreios").getSingleResult();
	    addressFound = findAddress(cep, urlCorreios);
	    if (addressFound.getResultado().equals("0"))
		throw new Exception();
	    
	} catch (Exception e) {
	    return eh.cepExceptionHandler(e, locale, cep);
	    
	}
	
	// Search for the city into RF database. If unsuccessful, then registers
	// the new city and its neighborhoods
	City city = null;
	try {
	    // Maybe it is necessary to add sql wildcard %
	    city = (City) em.createNamedQuery(City.FIND_CITY_BY_NAME).setParameter("name", addressFound.getCidade())
		    .getSingleResult();
	} catch (NoResultException e) {
	    city = registerCityAndNeighborhoods(addressFound);
	}
	
	Neighborhood neighborhood = null;
	try {
	    neighborhood = (Neighborhood) em.createNamedQuery(Neighborhood.FIND_NEIGHBORHOOD_BY_NAME_AND_CITY)
		    .setParameter("name", addressFound.getBairro()).setParameter("city", addressFound.getCidade())
		    .getSingleResult();
	    
	} catch (NoResultException e) {
	    if (city.getNeighborhoods().isEmpty()) {
		registerNeighborhoodsToNewCity(city);
	    } else {
		String answer = LocaleResource.getString(locale, "exception.cep.data", cep);
		log.log(Level.WARNING, answer);
		return RedeFoodAnswerGenerator.generateErrorAnswerString(401, answer);
	    }
	}
	
	try {
	    address.setNeighborhood(neighborhood);
	    String street = (addressFound.getTpLogradouro().isEmpty() ? "" : addressFound.getTpLogradouro() + " ")
		    + addressFound.getLogradouro();
	    address.setStreet(street);
	    address.setCity(city);
	    address.setZipcode(cep);
	    
	    return mapper.writeValueAsString(address);
	    
	} catch (Exception e) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.generic");
	    log.log(Level.SEVERE, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswerString(500, answer);
	}
	
    }
    
    /**
     * Method responsible for register a new city an its neighborhoods when a a
     * new subsidiary is registered into a non existing address.
     * 
     * @param addressFound
     * @return {@link City}
     */
    @Asynchronous
    private City registerCityAndNeighborhoods(Webservicecep addressFound) {
	log.log(Level.INFO, "New City " + addressFound.getCidade() + " - " + addressFound.getUf().toUpperCase() + ", "
		+ "being registered...");
	
	State state = (State) em.createNamedQuery(State.FIND_BY_SHORTNAME)
		.setParameter("shortName", addressFound.getUf()).getSingleResult();
	
	City city = new City(addressFound.getCidade(), state);
	
	em.persist(city);
	
	registerNeighborhoodsToNewCity(city);
	
	return city;
    }
    
    /**
     * Method responsible for retrieve an address from the Cep WS from a given
     * zipcode.
     * 
     * @param cep
     *            zipcode
     * @param urlCorreios
     *            url to query from
     * @return {@link Webservicecep}
     * @throws JAXBException
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    private static Webservicecep findAddress(String cep, String urlCorreios) throws JAXBException, JsonParseException,
    JsonMappingException, IOException {
	
	URL url = new URL("http://localhost:8080/cep?cep=" + cep + "&url=" + URLEncoder.encode(urlCorreios, "UTF-8"));
	return mapper.readValue(url, Webservicecep.class);
	
    }
    
    /**
     * Method responsible for querying the Cep Webservices and retrieve all
     * neighborhoods from a given city.
     * 
     * @param city
     */
    @SuppressWarnings("unchecked")
    private void registerNeighborhoodsToNewCity(City city) {
	log.log(Level.INFO, "Neighborhoods being fetch from CEP WS to city " + city.getName() + " - "
		+ city.getState().getShortName().toUpperCase());
	try {
	    URL url = new URL("http://localhost:8080/cep/neighborhood?state="
		    + URLEncoder.encode(city.getState().getShortName(), "UTF-8") + "&cityName="
		    + URLEncoder.encode(city.getName(), "UTF-8").replace("+", RedeFoodConstants.URL_SPACE));
	    
	    Map<String, List<String>> neighborhoods = mapper.readValue(url, Map.class);
	    
	    for (String neighborhoodName : neighborhoods.get("neighborhoods")) {
		em.persist(new Neighborhood(neighborhoodName, city));
	    }
	    
	    em.flush();
	    
	    log.log(Level.INFO,
		    neighborhoods.get("neighborhoods").size() + " neighborhoods registered to city " + city.getName()
		    + " - " + city.getState().getShortName().toUpperCase());
	    
	} catch (IOException e) {
	    log.log(Level.SEVERE, "Failed to fetch data from Cep WS.");
	} catch (Exception e) {
	    log.log(Level.SEVERE, "Failed to persist new neighborhoods to city " + city.getName());
	}
    }
}
