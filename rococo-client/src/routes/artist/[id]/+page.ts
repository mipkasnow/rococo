import type { PageLoad } from "./$types";
import { apiClient } from "$lib/api/apiClient";

export const load: PageLoad = async ({params}) => {

	const artistData = await apiClient.loadArtist(params.id);
	const paintingsData = await apiClient.loadPaintingsByAuthorId({authorId: params.id});
	
	return {
		artist: artistData,
		paintings: paintingsData,
	};
};