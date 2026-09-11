<x-app-layout>
    <x-slot name="header">
        <div class="flex justify-between">
            <h2 class="font-semibold text-xl text-gray-800 leading-tight">
                {{ __('Nurses') }}
            </h2>
        </div>
    </x-slot>
    <div class="py-12">
        <div class="max-w-7xl mx-auto sm:px-6 lg:px-8">
            <div class="bg-white overflow-hidden shadow-sm sm:rounded-lg">
                <div class="p-7 text-gray-900">
                    <x-splade-table :for="$nurses">
                        <x-splade-cell gender>
                            @if($item->gender)
                                {{ ('Male') }}
                            @else
                                {{ ('Female') }}
                            @endif
                        </x-splade-cell>
                        <x-splade-cell is_resigned>
                            @if($item->is_resigned)
                                {{ ('Resigned') }}
                            @else
                                {{ ('Not Resigned') }}
                            @endif
                        </x-splade-cell>
                        <x-splade-cell is_admin>
                            @if($item->is_admin)
                                {{ ('Admin') }}
                            @else
                                {{ ('User') }}
                            @endif
                        </x-splade-cell>
                        <x-splade-cell action>
                            <Link modal href="{{route('nurses.edit',$item)}}" class="font-bold text-indigo-600"> Edit </Link>
                               &nbsp
                            <Link href="{{route('nurses.destroy',$item)}}" method="DELETE"
                                  class="font-bold text-indigo-600"
                            confirm="Delete" confirm-text="Are you sure you want to delete">
                               Delete
                            </Link>
                        </x-splade-cell>
                     </x-splade-table>
                </div>
            </div>
        </div>
    </div>
</x-app-layout>
