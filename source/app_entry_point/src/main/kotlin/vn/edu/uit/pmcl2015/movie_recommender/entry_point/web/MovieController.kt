package vn.edu.uit.pmcl2015.movie_recommender.entry_point.web

import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.*
import vn.edu.uit.pmcl2015.movie_recommender.core.use_case.GetMovieCollectionUseCase
import vn.edu.uit.pmcl2015.movie_recommender.core.use_case.GetRecommendationsUseCase
import vn.edu.uit.pmcl2015.movie_recommender.core.use_case.UpdateMovieCollectionUseCase

data class MovieModel(val id: Int = 0, val title: String = "")

@Controller
@RequestMapping("movies")
class MovieController(private val getMovieCollectionUseCase: GetMovieCollectionUseCase,
                      private val getRecommendationsUseCase: GetRecommendationsUseCase,
                      private val updateMovieCollectionUseCase: UpdateMovieCollectionUseCase) {

  @PostMapping()
  fun updateMovieCollection(@RequestHeader(HttpHeaders.AUTHORIZATION) secretKey: String,
                            @RequestBody content: String): Any? {
    updateMovieCollectionUseCase.updateMovieCollectionByCsv(secretKey, content)
    return null
  }

  @PostMapping("images")
  fun updateMovieCollectionImages(@RequestHeader(HttpHeaders.AUTHORIZATION) secretKey: String,
                                  @RequestBody content: String): Any? {
    updateMovieCollectionUseCase.updateMovieCollectionImagesByCsv(secretKey, content)
    return null
  }

  @GetMapping()
  fun getAll(@RequestParam("search", defaultValue = "") search: String,
             model: ModelMap): String {
    val movies = getMovieCollectionUseCase.getMovieCollection(search)
    model.put("movies", movies.take(20))

    val list1 = getRecommendationsUseCase.getRecommendationsByMeanRatings(10)
    model.put("list1", list1)

    val list2 = getRecommendationsUseCase.getRecommendationsByDampedMeanRating(10)
    model.put("list2", list2)

    return "index"
  }

  @GetMapping("{id}")
  fun get(@PathVariable("id") id: Int, model: ModelMap): String {
    val movie = getMovieCollectionUseCase.getMovie(id)
    model.put("title", movie.title)
    model.put("backdrop", movie.backdrop)

    val list1 = getRecommendationsUseCase.getRecommendationsByDampedMeanRating(10)
    val list2 = getRecommendationsUseCase.getRecommendationsByBasicAssociations(10, id)

    model.put("list1", list1)
    model.put("list2", list2)
    return "single"
  }
}