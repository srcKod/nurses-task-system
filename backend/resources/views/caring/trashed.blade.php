<x-app-layout>
    <x-slot name="header">
        <div class="flex justify-between">
            <h2 class="font-semibold text-xl text-gray-800 leading-tight">
                {{ __('Trashed Carings') }}
            </h2>
        </div>
    </x-slot>
    <div class="py-12">
        <div class="max-w-7xl mx-auto sm:px-6 lg:px-8">
            <div class="bg-white overflow-hidden shadow-sm sm:rounded-lg">
                <div class="p-7 text-gray-900">
                    <x-splade-table :for="$carings">
                        <x-splade-cell nurse_id>
                            {{ $item->nurse->name }}
                        </x-splade-cell>
                        <x-splade-cell caringtype_id>
                            {{ $item->caringtype->name }}
                        </x-splade-cell>
                        <x-splade-cell patient_id>
                            {{ $item->patient->name }}
                        </x-splade-cell>
                        <x-splade-cell is_finished>
                            @if($item->is_finished)
                                {{ ('Finished') }}
                            @else
                                {{ ('Waiting') }}
                            @endif
                        </x-splade-cell>
                        <x-splade-cell action>
{{--                            @dd($item->id)--}}
                            <Link modal href="{{route('carings.edit-trashed',$item->id)}}" class="font-bold text-indigo-600"> Edit </Link>
                            &nbsp
                            <Link href="{{route('carings.prune',$item->id)}}" class="font-bold text-indigo-600"
                                  method="DELETE" confirm="Delete" confirm-text="Are you sure you want to delete"> Prune </Link>
                            &nbsp
                            @if($item->deleted_at)
                                <Link method="PATCH" href="{{route('carings.restore',$item->id)}}" class="font-bold text-indigo-600"> Restore </Link>
                            @endif
                        </x-splade-cell>
                    </x-splade-table>
                </div>
            </div>
        </div>
    </div>
</x-app-layout>
