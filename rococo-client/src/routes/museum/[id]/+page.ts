import type { PageLoad } from "./$types";
import {apiClient} from "$lib/api/apiClient";


export const load: PageLoad = async ({params}) => {
    const data = await apiClient.loadMuseum(params.id);
    return {
        ...data,
    };
};