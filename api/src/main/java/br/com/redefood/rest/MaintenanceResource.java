package br.com.redefood.rest;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.com.redefood.model.Neighborhood;
import br.com.redefood.service.GMapService;

@Stateless
public class MaintenanceResource {
	@Inject
	private EntityManager em;
	@Inject
	private Logger log;

	// TODO: works only in standalone mode
	@Schedule(dayOfMonth = "*", hour = "05", minute = "05", second = "05", persistent = false)
	private void cleanTokens() {
		// 2 days because we want to keep record of user's ip address, in case
		// something happens.
		String queryOld = "DELETE FROM Login WHERE lastSeen < (NOW() - INTERVAL 3 DAY)";
		log.log(Level.INFO, String.valueOf(em.createNativeQuery(queryOld).executeUpdate())
				+ " tokens removed during daily maintenance scheduler.");
	}

	@SuppressWarnings("unchecked")
	@Schedule(dayOfMonth = "02", hour = "05", minute = "45", second = "15", persistent = false)
	private void updateNeighborhoodsCoordinates() {

		try {
			List<Neighborhood> neighborhoods = em.createNamedQuery(Neighborhood.FIND_ALL_NEIGHBORHOOD).getResultList();

			for (Neighborhood neighborhood : neighborhoods) {
				Map<String, String> geocode = GMapService.getGeocode(neighborhood.getName() + ", "
						+ neighborhood.getIdCity().getName() + ", " + neighborhood.getIdCity().getState().getName());

				if (geocode.get("lat") != null) {
					neighborhood.setLat(geocode.get("lat"));
				}
				if (geocode.get("lng") != null) {
					neighborhood.setLng(geocode.get("lng"));
				}

				em.merge(neighborhood);
				em.flush();
			}
			log.log(Level.INFO, "Neighborhoods coordinates updated");
		} catch (Exception e) {
			log.log(Level.INFO, "Failed to update neighborhoods coordinates");
		}
	}
}
