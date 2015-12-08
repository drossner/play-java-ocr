package modules.database;

import modules.database.entities.*;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by florian on 02.12.15.
 */
public class JobController extends DatabaseController<Job, Object> {

    @Transactional
    public void persistJob(Job job, Image image, LayoutConfig layoutConfig, User user) {
        JPA.em().persist(image);
        JPA.em().persist(layoutConfig);

        job.setImage(image);
        job.setLayoutConfig(layoutConfig);
        job.setUser(selectEntity(User.class, "eMail", user.geteMail()));

        JPA.em().persist(job);
    }

    public List<Job> getJobsFromUser(String userName) {
        User user = selectEntity(User.class, "eMail", userName);

        return selectEntityList(Job.class, user);
    }

    public Job getJobById(int id) {
        return selectEntity(Job.class, "id", Integer.toString(id));
    }

    public List<Language> getAllCountryLanguages() throws Throwable {
        return JPA.withTransaction(() -> {
                    List<Language> rc = new ArrayList<>();
                    List<Country> countryList = selectEntityList(Country.class, null);

            rc.addAll(countryList.stream().map(country -> getCountryImpl(country.getCountry())).collect(Collectors.toList()));

            return rc;
                }
        );
    }

    private Language getCountryImpl(CountryImpl country) {
        Language language = new Language();
        language.isoCode = country.getIsoCode();
        language.name = country.getName();
        return language;
    }

    public List<LayoutConfig> getStandardTemplates() {
        return selectEntityList(LayoutConfig.class, null);
    }

    private class Language{
        public String name;

        public String isoCode;
    }
}
